package com.cgy.cgy.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.Balance;
import com.stripe.model.Card;
import com.stripe.model.Charge;
import com.stripe.model.ChargeCollection;
import com.stripe.model.Customer;
import com.stripe.model.EphemeralKey;
import com.stripe.model.PaymentIntent;
import com.stripe.model.PaymentSource;
import com.stripe.model.Token;
import com.stripe.net.RequestOptions;
import com.stripe.param.EphemeralKeyCreateParams;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class StripeService {
	// Test mode secret keys have the prefix sk_test_
	// 客户端用 pk_test 后端用 sk_test
//	private final String STRIP_API_KEY = "pk_test_sxgqTrwz5alXy47e9YBkJMm5000wQJsBtw";
	private final String STRIP_API_KEY = "sk_test_0oamHqJqUkai2LqEGL8T1yo700RB7LLmi1";

	// 可以给每个请求通过setIdempotencyKey()加上key 防止重复请求（24小时过期）
	private final String idempotencyKey = UUID.fromString("146bbe6b-fffd-408a-8159-6d60d0d26c01").toString();
	RequestOptions options;// 请求配置

	public static void main(String[] args) {
		System.out.println(UUID.randomUUID());
	}

	public StripeService() {
		// Set your secret key: remember to change this to your live secret key in
		// production
		// See your keys here: https://dashboard.stripe.com/account/apikeys
		Stripe.apiKey = STRIP_API_KEY;
		options = RequestOptions.builder().build();
		log.info("Stripe API_VERSION {}", Stripe.API_VERSION);
		log.info("set stripe api key {}", Stripe.apiKey);
	}

	/**
	 * 枚举所有的customer
	 * 
	 * @throws StripeException
	 */
	public void listCustomers() throws StripeException {
//		log.info("stripe Balance {}", Balance.retrieve().toString());

		Map<String, Object> customerParams = new HashMap<String, Object>();
		customerParams.put("limit", "3");

		List<Customer> list = Customer.list(customerParams).getData();
		log.info("listCustomers size {}", list == null ? null : list.size());

		Iterable<Customer> itCustomers = Customer.list(customerParams).autoPagingIterable();

		for (Customer customer : itCustomers) {
			// Do something with customer
			log.info("listCustomers customer name \n{}", customer.getName());
		}
	}

	/**
	 * 找到或者创建一个customer
	 * 
	 * @return
	 * @throws StripeException
	 */
	public Customer findOrCreateCustomer(String customerId) throws StripeException {
		log.info("findOrCreateCustomer start！ customerId {}", customerId);
		Customer customer = null;
		if (customerId != null && customerId.length() > 0) {
			customer = Customer.retrieve(customerId);// customer 必定不为空，若不存在会抛出错误
			log.info("findOrCreateCustomer found customer \n{}", customer.toString());
			return customer;
		}

		// Create a Customer:
		Map<String, Object> customerParams = new HashMap<>();
//		customerParams.put("source", "tok_mastercard");
		customerParams.put("name", "testName");
		customerParams.put("phone", "15051111111");
		customerParams.put("email", "paying.test@example.com");
		customer = Customer.create(customerParams, options);
		log.info("findOrCreateCustomer create customer {}", customer.toString());
		log.info("findOrCreateCustomer create customer {}", customer.getName());
		return customer;
	}

	public Customer updateCustomer(String customerId, String name) throws StripeException {
		// Set your secret key: remember to change this to your live secret key in
		// production
		// See your keys here: https://dashboard.stripe.com/account/apikeys

		// 此处如果没有检索到会抛出com.stripe.exception.InvalidRequestException，所以无需判空
		Customer customer = Customer.retrieve(customerId);
		log.info("updateCustomer {}", customer.getName());

		Map<String, Object> params = new HashMap<>();
		params.put("name", "updateName");
		customer = customer.update(params);

		log.info("updateCustomer ok {}", customer.getName());
		return customer;
	}

	/**
	 * 添加信用卡
	 * 
	 * @throws StripeException
	 */
	public Card addCreditCard(String customerId, String tok_mastercard) throws StripeException {
		Customer customer = Customer.retrieve(customerId);
		log.info("addCreditCard customer: {}", customer.getName());

		Map<String, Object> params = new HashMap<String, Object>();
		params.put("source", tok_mastercard);
		Card source = (Card) customer.getSources().create(params);
		log.info("addCreditCard PaymentSource: {}", source.toString());
		return source;
	}

//	创建token，此操作应该放在app端，如果在backend中调用了，就无法使用stripe的radar（一个人工智能的防诈骗api）
//	In most cases, you should use our recommended payments integrations instead of using the API.
//此api第一次调用会导致com.stripe.exception.InvalidRequestException ：You must verify a phone number on your Stripe account before you can send raw credit card numbers to the Stripe API. You can avoid this requirement by using Stripe.js, the Stripe mobile bindings, or Stripe Checkout. For more information, see https://dashboard.stripe.com/phone-verification.
	public Token testCreateToken(String cardNum) throws StripeException {
		Map<String, Object> tokenParams = new HashMap<String, Object>();
		Map<String, Object> cardParams = new HashMap<String, Object>();
		cardParams.put("number", cardNum);
		cardParams.put("exp_month", 4);
		cardParams.put("exp_year", 2020);
		cardParams.put("cvc", "314");
		tokenParams.put("card", cardParams);

		return Token.create(tokenParams);
	}

	public void retreiveToken(String tokenStr) throws StripeException {
		Token token = Token.retrieve(tokenStr);// "tok_1EU8Rv2eZvKYlo2CGyRTOr0w"
		log.info("retreiveToken token: {}", token.toString());
	}

	/**
	 * 获取用户所有的卡片
	 * 
	 * @param customerId
	 * @throws StripeException
	 */
	public List<PaymentSource> listAllCards(String customerId) throws StripeException {

		Map<String, Object> cardParams = new HashMap<String, Object>();
		cardParams.put("limit", 5);
//		cardParams.put("object", "card");//前端paymentMethodsActivity添加的卡似乎不属于card，用这个会查不到
		List<PaymentSource> cards = Customer.retrieve(customerId).getSources().list(cardParams).getData();

		log.info("listAllCards cards size : {}", cards == null ? null : cards.size());
		if (cards != null) {
			for (PaymentSource card : cards) {
				log.info("\nlistAllCards card id : {}", card.getId());
			}
		}
		return cards;
//		Card source = (Card) customer.getSources().retrieve(card_1EU6pK2eZvKYlo2CDrsnPJ7J);
	}

	/**
	 * 根据id查找某一个卡
	 * 
	 * @param customerId
	 * @param cardId
	 * @return
	 * @throws StripeException
	 */
	public Card retreiveCard(String customerId, String cardId) throws StripeException {
		Card card = (Card) Customer.retrieve(customerId).getSources().retrieve(cardId);
		log.info("\nretreiveCard cards   info : {}", card.toString());
		return card;
	}

	/**
	 * 删除某一张卡
	 * 
	 * @param customerId
	 * @param cardId
	 * @throws StripeException
	 */
	public void deleteCard(String customerId, String cardId) throws StripeException {
		Customer customer = Customer.retrieve(customerId);
		Card card = (Card) customer.getSources().retrieve(cardId);
		card.delete();
	}

	/**
	 * 发起支付
	 * 
	 * @param customerId The ID of an existing customer that will be charged in this
	 *                   request.
	 * @param source     A payment source to be charged. This can be the ID of a
	 *                   card (i.e., credit or debit card), a bank account, a
	 *                   source, a token, or a connected account. For certain
	 *                   sources—namely, cards, bank accounts, and attached
	 *                   sources—you must also pass the ID of the associated
	 *                   customer.
	 * @param amount     A positive integer representing how much to charge in the
	 *                   smallest currency unit ，人民币似乎必须是整数(此处似乎最少500)
	 * @throws StripeException
	 */
	public Charge charge(String customerId, String source, int amount) throws StripeException {
		Map<String, Object> chargeParams = new HashMap<String, Object>();
		chargeParams.put("amount", amount);// 2000
		chargeParams.put("currency", "CNY");// usd
		chargeParams.put("description", "Charge for jenny.rosen@example.com");
		chargeParams.put("source", source);
		chargeParams.put("customer", customerId);
//		chargeParams.put("capture", true);// 必须capture才会真正的 完成交易 不然7天后会自动退款,默认是true
		// ^ obtained with Stripe.js
		Charge charge = Charge.create(chargeParams);
		log.info("\ncharge : {}", charge.toString());
		return charge;
	}

	/**
	 * @param customerId Only return charges for the customer specified by this
	 *                   customer ID.
	 * @throws StripeException
	 */
	public ChargeCollection listCharges(String customerId) throws StripeException {
		Map<String, Object> chargeParams = new HashMap<String, Object>();
		chargeParams.put("limit", "3");
		chargeParams.put("customer", customerId);

		ChargeCollection chargeCollection = Charge.list(chargeParams);
		List<Charge> charges = chargeCollection.getData();
		if (charges != null) {
			for (Charge cha : charges) {
				log.info("\nlistCharges charge amount : {}", cha.getAmount());
			}
		}

		return chargeCollection;
	}

	public EphemeralKey createEphemeralKey(String customerId, String stripeVersion) throws Exception {
		RequestOptions op = RequestOptions.builder().setStripeVersionOverride(stripeVersion).build();
		EphemeralKeyCreateParams params = EphemeralKeyCreateParams.builder().setCustomer(customerId).build();
		EphemeralKey key = EphemeralKey.create(params, op);
		log.info("\ncreateEphemeralKey id: {}", key.getId());
		return key;
	}

	/**
	 * AUTOMATIC CONFIRMATION Step
	 * </p>
	 * 1: Create a PaymentIntent on the server
	 * 
	 * We recommend creating a PaymentIntent as soon as the amount is known, such as
	 * when the customer begins the checkout process.
	 * 
	 * Step 2: Pass the PaymentIntent’s client secret to the client
	 * 
	 * The PaymentIntent object contains a client secret, a unique key that you need
	 * to pass to Stripe.js on the client side in order to create a charge.
	 * 
	 * Step 3: Collect payment method details on the client
	 * 
	 * Step 4: Submit the payment to Stripe from the client
	 * 
	 * Step 5: Asynchronously fulfill the customer’s order
	 * 
	 * @param amount
	 * @param currentcy
	 * @throws StripeException
	 */
	public PaymentIntent createPaymentIntentForAutoConfirm(int amount, String currency) throws StripeException {
		Map<String, Object> paymentintentParams = new HashMap<String, Object>();
		paymentintentParams.put("amount", amount);// amount可以通过update来更新
		paymentintentParams.put("currency", currency);// usd CNY

		PaymentIntent intent = PaymentIntent.create(paymentintentParams);
		log.info("\n createPaymentIntentForAutoConfirm  : {}", intent.toJson());
		return intent;
	}
}

// Charge the Customer instead of the card:
//Map<String, Object> customerParams = new HashMap<>();
//customerParams.put("amount", 1000);
//customerParams.put("currency", "usd");
//customerParams.put("customer", customer.getId());
//Charge charge = Charge.create(customerParams);

// YOUR CODE: Save the customer ID and other info in a database for later.

// When it's time to charge the customer again, retrieve the customer ID.
//Map<String, Object> params = new HashMap<>();
//params.put("amount", 1500); // $15.00 this time
//params.put("currency", "usd");
//params.put("customer", customerId); // Previously stored, then retrieved
//Charge charge = Charge.create(params);
