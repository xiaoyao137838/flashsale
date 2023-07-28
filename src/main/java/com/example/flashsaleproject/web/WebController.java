package com.example.flashsaleproject.web;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.servlet.ModelAndView;

import com.example.flashsaleproject.jpa.FlashSaleRepository;
import com.example.flashsaleproject.jpa.OrderRepository;
import com.example.flashsaleproject.jpa.ProductRepository;
import com.example.flashsaleproject.jpa.UserRepository;
import com.example.flashsaleproject.models.FlashSale;
import com.example.flashsaleproject.models.FlashUser;
import com.example.flashsaleproject.models.FlashOrder;
import com.example.flashsaleproject.models.Product;
import com.example.flashsaleproject.redis.RedisService;
import com.example.flashsaleproject.security.SecurityUtils;
import com.example.flashsaleproject.service.FlashSaleService;
import com.example.flashsaleproject.service.OrderService;
import com.example.flashsaleproject.utils.LoginVM;
import com.example.flashsaleproject.utils.RegisterVM;
import com.google.gson.Gson;

import io.micrometer.common.util.StringUtils;

@Controller
@SessionAttributes("user")
public class WebController {
	
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	
	private FlashSaleRepository flashSaleRepository;
	
	private OrderRepository orderRepository;
	
	private ProductRepository productRepository;
	
	private UserRepository userRepository;
	
	private RedisService redisService;
	
	private FlashSaleService flashSaleService;
	
	private OrderService orderService;
	
	private PasswordEncoder passwordEncoder;
	
	public WebController(FlashSaleRepository flashSaleRepository, OrderRepository orderRepository,
			ProductRepository productRepository, UserRepository userRepository, RedisService redisService,
			FlashSaleService flashSaleService, OrderService orderService, PasswordEncoder passwordEncoder) {
		super();
		this.flashSaleRepository = flashSaleRepository;
		this.orderRepository = orderRepository;
		this.productRepository = productRepository;
		this.userRepository = userRepository;
		this.redisService = redisService;
		this.flashSaleService = flashSaleService;
		this.orderService = orderService;
		this.passwordEncoder = passwordEncoder;
	}
	
	Gson gson = new Gson();
//
//    String jsonStr = gson.toJson(foo);
//    Foo result = gson.fromJson(jsonStr, Foo.class);
	
	@GetMapping("/")
	public String home() {
		return "redirect:/flashSales";
	}
	
	@GetMapping("/register")
	public String registerRender() {
		return "register";
	}
	
	@PostMapping("/register")
	public String register(RegisterVM registerVM) {
		FlashUser user = new FlashUser();
		
		try {
			user.setUsername(registerVM.getUsername());
			user.setEmail(registerVM.getEmail());
			
			String encryptedPassword = passwordEncoder.encode(registerVM.getPassword());
			user.setPassword(encryptedPassword);
			user.setCreatedBy(registerVM.getUsername());
			userRepository.save(user);
		} catch (Exception e) {
			logger.error("The error is " + e.getMessage());
			return "register";
		}

		logger.info("user created: {}", user);
		return "redirect:/login";
	}

	@GetMapping("/login")
	public String loginRender() {
		return "login";
	}
	
	@PostMapping("/login")
	public String register(@RequestBody LoginVM loginVM) {
		return "redirect:/flashSales";
	}
	
	@RequestMapping("/addFlashSale")
	public String addFlashSale() {
		return "add_activity";
	}
	
	@PostMapping("/addFlashSaleAction")
	public String addFlashSaleAction(
			@RequestParam("name") String name,
			@RequestParam("productId") long productId,
			@RequestParam("newPrice") BigDecimal newPrice,
			@RequestParam("oldPrice") BigDecimal oldPrice,
			@RequestParam("totalQuantity") int totalQuantity,
			@RequestParam("startTime") String startTime,
			@RequestParam("endTime") String endTime,
			ModelMap model
			) throws Exception {
		logger.info("add new Flash sale: {}", name);
		logger.info(startTime + " to " + endTime);
		startTime = startTime.substring(0, 10) + " " + startTime.substring(11);
		endTime = endTime.substring(0, 10) + " " + endTime.substring(11);
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd hh:mm");
		Optional<Product> optionalProduct = productRepository.findById(productId);
		
		if (optionalProduct.isEmpty()) {
			throw new Exception("This product does not exist");
		}
		Product product = optionalProduct.get();
		
		FlashSale flashSale = new FlashSale();
		flashSale.setName(name);
		flashSale.setProduct(product);
		flashSale.setNewPrice(newPrice);
		flashSale.setOldPrice(oldPrice);
		flashSale.setStartTime(format.parse(startTime));
		flashSale.setEndTime(format.parse(endTime));
		flashSale.setTotalQuantity(totalQuantity);
		flashSale.setAvailableQuantity(totalQuantity);
		flashSale.setLockedQuantity(0);
		flashSale.setStatus(true);
		
		flashSale = flashSaleRepository.save(flashSale);
		redisService.addFlashSaleQuantity(flashSale.getId(), 
				flashSale.getAvailableQuantity());
		redisService.addFlashSaleInfo(flashSale.getId(), flashSale);
		redisService.addProductInfo(product.getId(), product);
		
		model.addAttribute("flashSale", flashSale);
		return "add_success";
	}
	
	@RequestMapping("/flashSales")
	public String flashSaleList(ModelMap model) {
		
		String username = SecurityUtils.getCurrentUsername().get();
		FlashUser user = userRepository.findOneWithAuthoritiesByUsername(username).get();
		List<FlashSale> flashSales = flashSaleRepository.findAll();

		model.addAttribute("user", user);
		model.addAttribute("flashSales", flashSales);
		return "seckill_list";
	}
	
	@RequestMapping("/flashSales/{flashSaleId}")
	public String flashSale(ModelMap model, 
		@PathVariable long flashSaleId) throws Exception {
		
		
		FlashSale flashSale;
		Product product;
		
		String flashSaleKey = "java-flash:flashsale:" + flashSaleId;
		String flashSaleInfo = redisService.getFlashSaleInfo(flashSaleKey);
		if (StringUtils.isNotEmpty(flashSaleInfo)) {
			logger.info("FlashSale from Redis: " + flashSaleInfo);
			flashSale = gson.fromJson(flashSaleInfo, FlashSale.class);
		} else {
			Optional<FlashSale> optionalFlashSale = flashSaleRepository.findById(flashSaleId);
			if (optionalFlashSale.isEmpty()) {
				throw new Exception("This flashSale does not exist.");
			} 
			flashSale = optionalFlashSale.get();

		}
		
		String productKey = "java-flash:product:" + flashSale.getProduct().getId();
		String productInfo = redisService.getProductInfo(productKey);
		if (StringUtils.isNotEmpty(productInfo)) {
			logger.info("Product from Redis: " + productInfo);
			product = gson.fromJson(productInfo, Product.class);
		} else {
			Optional<Product> optionalProduct = productRepository.findById(flashSale.getProduct().getId());
			if (optionalProduct.isEmpty()) {
				throw new Exception("This product does not exist.");
			} 
			product = optionalProduct.get();

		}

		model.addAttribute("flashSale", flashSale);
		model.addAttribute("product", product);
		model.addAttribute("newPrice", flashSale.getNewPrice());
		model.addAttribute("oldPrice", flashSale.getOldPrice());
		model.addAttribute("productId", product.getId());
		model.addAttribute("productName", product.getName());
		model.addAttribute("productDescription",
			product.getDescription());
		
		return "seckill_item";
	}
	
	@RequestMapping("/purchase/{userId}/{flashSaleId}")
	public ModelAndView purchaseFlashSale(
		@PathVariable long userId,
		@PathVariable long flashSaleId) {
		
		ModelAndView modelAndView = new ModelAndView();
		
		boolean stockValidateResult = false;
		
		try {
			if (redisService.isInLimitMember(flashSaleId, userId)) {
				modelAndView.addObject("resultInfo", 
					"Sorry, you have purchased before.");
				modelAndView.setViewName("seckill_result");
				return modelAndView;
			} 
			
			stockValidateResult = flashSaleService.stockValidator(
				flashSaleId);
			
			if (stockValidateResult) {
				FlashOrder order = orderService.createOrder(
					flashSaleId, userId);
				redisService.addLimitMember(flashSaleId, userId);
				
				modelAndView.addObject("resultInfo", 
					"""
						You purchase successfully. The order is 
						being created. Order number:	
					"""
					+ order.getOrderNo());
				modelAndView.addObject("orderNo", order.getOrderNo());
			} else {
				modelAndView.addObject("resultInfo", 
					"Sorry, the stock is not enough.");
			}
		} catch (Exception e) {
			logger.error("The system is something wrong: {}", e.toString());
			modelAndView.addObject("resultInfo", "Fail to purchase");
		}
		
		modelAndView.setViewName("seckill_result");
		return modelAndView;
	}
	
	@RequestMapping("/flashSale/orderQuery")
	public ModelAndView orders() {
		logger.info("Order current user's orders");
		ModelAndView modelAndView = new ModelAndView();
		FlashUser currentUser = SecurityUtils.getCurrentUser().get();

		List<FlashOrder> orders = orderRepository.findByUser(currentUser);
		List<FlashOrder> unpaiedOrders = orders.stream()
				.filter(order -> 
						order.getStatus() != 2)
				.collect(Collectors.toList());
		
		List<FlashOrder> paiedOrders = orders.stream()
				.filter(order -> 
						order.getStatus() == 2)
				.collect(Collectors.toList());
		
		modelAndView.addObject("unpaiedOrders", unpaiedOrders);
		modelAndView.addObject("paiedOrders", paiedOrders);
		modelAndView.setViewName("order_list");
		return modelAndView;
	}
	
	@RequestMapping("/flashSale/orderQuery/{orderNo}")
	public ModelAndView orderQuery(@PathVariable String orderNo) {
		logger.info("Order querying. OrderNo: " + orderNo);
		ModelAndView modelAndView = new ModelAndView();
		
		Optional<FlashOrder> order = orderRepository.findByOrderNo(orderNo);
		if (order.isEmpty()) {
			modelAndView.setViewName("wait");
		} else {
			FlashOrder orderQuery = order.get();
			modelAndView.setViewName("order");
			modelAndView.addObject("order", orderQuery);

			FlashSale flashSale = orderQuery.getFlashSale();
			modelAndView.addObject("flashSale", flashSale);
		}
		
		return modelAndView;
	}
	
	@RequestMapping("/flashSale/payOrder/{orderNo}")
	public String payOrder(@PathVariable String orderNo, ModelMap model)
	throws Exception {
		logger.info("To pay the order: {}", orderNo);
		Optional<FlashOrder> orderOpt = orderRepository.findByOrderNo(orderNo);
		
		if (orderOpt.isEmpty()) {
			return "redirect:/flashSale/orderQuery";
		}
		
		if (orderOpt.get().getStatus() != 0) {
			return "redirect:/flashSale/orderQuery/" + orderNo;
		}
		
		Optional<String> username = SecurityUtils.getCurrentUsername();

		Optional<FlashUser> user = username.flatMap(name -> {
			return userRepository.findOneWithAuthoritiesByUsername(name);
		});
		
		if (user.isPresent()) {
			try {
				orderService.payOrderProcess(orderNo, user.get().getId());
				
				model.addAttribute("resultInfo", "The payment is done successfully!");
				return "seckill_result"; 
			} catch (Exception e) {
				logger.error(e.getMessage());
			}
		}
		return "error";
	}
	
	@ResponseBody
	@RequestMapping("/flashSale/getSystemTime")
	public String getSystemTime() {
		Date currentTime = new Date();
		logger.info("current time is {}", currentTime);
		SimpleDateFormat ft = new SimpleDateFormat(
			"yyyy-MM-dd HH:mm:ss");
		String date = ft.format(new Date());
		
		return date;
	}

}
