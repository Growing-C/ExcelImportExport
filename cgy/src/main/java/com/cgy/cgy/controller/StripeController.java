package com.cgy.cgy.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.cgy.cgy.service.StripeService;
import com.stripe.exception.StripeException;
import com.stripe.model.Card;
import com.stripe.model.Charge;
import com.stripe.model.ChargeCollection;
import com.stripe.model.Customer;
import com.stripe.model.EphemeralKey;
import com.stripe.model.PaymentIntent;
import com.stripe.model.PaymentSource;
import com.stripe.model.Token;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
public class StripeController {
	@Autowired // 与service层进行交互
	private StripeService mStripeService;

	// 已创建的可用 customer id：cus_Ey39ZcB7ww7vWV
	// http://localhost:8181/stripe/customer/create?customerId=cus_Ey39ZcB7ww7vWV
	@RequestMapping("/stripe/customer/create")
	public String createCustomer(@RequestParam("customerId") String customerId) {
		try {
			mStripeService.listCustomers();

			Customer customer = mStripeService.findOrCreateCustomer(customerId);
//			return String.format("createCustomer success !  name :%s", customer.getName());
			return customer.toJson();
		} catch (StripeException e) {
			e.printStackTrace();
			return "Error:" + e.getMessage();
		}

	}

	// http://localhost:8181/stripe/customer/update?customerId=cus_Ey39ZcB7ww7vWV
	@RequestMapping("/stripe/customer/update")
	public String updateCustomer(@RequestParam("customerId") String customerId) {
		if (customerId == null || customerId.length() == 0) {
			return String.format("Error: customerId is null");
		}
		try {
			Customer customer = mStripeService.updateCustomer(customerId, null);
//			return String.format("UpdateCustomer success !  name :%s", customer.getName());
			return customer.toJson();
		} catch (StripeException e) {
			e.printStackTrace();
			return "Error:" + e.getMessage();
		}
	}

	// 所有测试数据 见 https://stripe.com/docs/testing
	// 可用token:tok_1EU9t3C6kLRXXaxJtGC8Brjy
	// http://localhost:8181/stripe/customer/addCard?customerId=cus_Ey39ZcB7ww7vWV&token=tok_1EU9t3C6kLRXXaxJtGC8Brjy
	@RequestMapping("/stripe/customer/addCard")
	public String addCreditCard(@RequestParam("customerId") String customerId,
			@RequestParam("token") String cardToken) {
		if (customerId == null || customerId.length() == 0) {
			return String.format("Error: customerId is null");
		}
		if (cardToken == null || cardToken.length() == 0) {
			return String.format("Error: cardToken is null");
		}

		try {
			Card card = mStripeService.addCreditCard(customerId, cardToken);
			return card.toJson();
		} catch (StripeException e) {
			e.printStackTrace();
			return "Error:" + e.getMessage();
		}
	}

	// available card id: card_1EU9t3C6kLRXXaxJv6pDO2jF
	// http://localhost:8181/stripe/customer/getCard?customerId=cus_Ey39ZcB7ww7vWV&cardId=
	@RequestMapping("/stripe/customer/getCard")
	public String getCustomerCardInfo(@RequestParam("customerId") String customerId,
			@RequestParam("cardId") String cardId) {
		if (customerId == null || customerId.length() == 0) {
			return String.format("Error: customerId is null");
		}
		try {
			if (cardId == null || cardId.length() == 0) {
				List<PaymentSource> cards = mStripeService.listAllCards(customerId);
				return String.format("listAllCards size %s", cards == null ? 0 : cards.size());
			}

			Card card = mStripeService.retreiveCard(customerId, cardId);
			return card.toJson();
		} catch (StripeException e) {
			e.printStackTrace();
			return "Error:" + e.getMessage();
		}
	}

	// available card id: card_1EU9t3C6kLRXXaxJv6pDO2jF
	// http://localhost:8181/stripe/customer/deleteCard?customerId=cus_Ey39ZcB7ww7vWV&cardId=
	@RequestMapping("/stripe/customer/deleteCard")
	public String deleteCard(@RequestParam("customerId") String customerId, @RequestParam("cardId") String cardId) {
		if (customerId == null || customerId.length() == 0) {
			return String.format("Error: customerId is null");
		}
		if (cardId == null || cardId.length() == 0) {
			return String.format("Error: cardToken is null");
		}
		try {

			mStripeService.deleteCard(customerId, cardId);
			return "Delete succeed!";
		} catch (StripeException e) {
			e.printStackTrace();
			return "Error:" + e.getMessage();
		}
	}

	// 可用的测试卡号 4242424242424242 4000056655665556 5555555555554444 371449635398431
	// 真卡 4477570005382005 6210676802084484923
	// http://localhost:8181/stripe/customer/createToken?cardNum=4000056655665556
	@RequestMapping("/stripe/customer/createToken")
	public String testCreateToken(@RequestParam("cardNum") String cardNum) {
		if (cardNum == null || cardNum.length() == 0) {
			return String.format("Error: cardNum is null");
		}
		try {
			Token token = mStripeService.testCreateToken(cardNum);
			log.info("createToken token id {}", token.getId());
			mStripeService.retreiveToken(token.getId());
			return token.toJson();
		} catch (StripeException e) {
			e.printStackTrace();
			return "Error:" + e.getMessage();
		}
	}

	// http://localhost:8181/stripe/customer/charge?customerId=cus_Ey39ZcB7ww7vWV&source=card_1EU9t3C6kLRXXaxJv6pDO2jF&amount=10
	@RequestMapping("/stripe/customer/charge")
	public String charge(@RequestParam("customerId") String customerId, @RequestParam("source") String source,
			@RequestParam("amount") int amount) {
		if (customerId == null || customerId.length() == 0) {
			return String.format("Error: customerId is null");
		}
		if (source == null || source.length() == 0) {
			return String.format("Error: source is null");
		}
		if (amount <= 0) {
			return String.format("Error: amount is not valid");
		}
		try {
			Charge charge = mStripeService.charge(customerId, source, amount);
//			return String.format("charge succeed currency: %s source id :%s", charge.getCurrency(),
//					charge.getSource().getId());
			return charge.toJson();
		} catch (StripeException e) {
			e.printStackTrace();
			return "Error:" + e.getMessage();
		}
	}

	// http://localhost:8181/stripe/customer/listCharges?customerId=cus_Ey39ZcB7ww7vWV
	@RequestMapping("/stripe/customer/listCharges")
	public String listCharges(@RequestParam("customerId") String customerId) {
		if (customerId == null || customerId.length() == 0) {
			return String.format("Error: customerId is null");
		}

		try {
			ChargeCollection chargeCollection = mStripeService.listCharges(customerId);
//			return String.format("charges size %s", charges == null ? 0 : charges.size());
			return chargeCollection.toJson();
		} catch (StripeException e) {
			e.printStackTrace();
			return "Error:" + e.getMessage();
		}
	}

	// http://192.168.34.19:8181/stripe/customer/createEphemeralKey?customerId=cus_Ey39ZcB7ww7vWV&version=2017-06-05
	// http://localhost:8181/stripe/customer/createEphemeralKey?customerId=cus_Ey39ZcB7ww7vWV&version=2017-06-05
	@RequestMapping("/stripe/customer/createEphemeralKey")
	public String createEphemeralKey(@RequestParam("customerId") String customerId,
			@RequestParam("version") String version) {
		if (version == null || version.length() == 0) {
			return String.format("Error: version is null");
		}
		log.info("version is {}", version);
		if (customerId == null || customerId.length() == 0) {
//			return String.format("Error: customerId is null");
			customerId = "cus_Ey39ZcB7ww7vWV";// TODO:此id待决定 是否是前端传，后台其实可以根据帐号获取到id
		}
		try {
			EphemeralKey key = mStripeService.createEphemeralKey(customerId, version);
			return key.toJson();
		} catch (Exception e) {
			e.printStackTrace();
			return "Error:" + e.getMessage();
		}
	}

	// http://192.168.34.19:8181/stripe/customer/createPaymentIntentForAutoConfirm?currency=CNY&amount=501
	// http://localhost:8181/stripe/customer/createPaymentIntentForAutoConfirm?currency=CNY&amount=501
	@RequestMapping("/stripe/customer/createPaymentIntentForAutoConfirm")
	public String createPaymentIntentForAutoConfirm(@RequestParam("currency") String currency,
			@RequestParam("amount") int amount) {
		if (currency == null || currency.length() == 0) {
			return String.format("Error: currency is null");
		}
		if (amount <= 0) {
			return String.format("Error: amount is not valid");
		}
		try {
			PaymentIntent intent = mStripeService.createPaymentIntentForAutoConfirm(amount, currency);
//			return String.format("charge succeed currency: %s source id :%s", charge.getCurrency(),
//					charge.getSource().getId());
			return intent.toJson();
		} catch (StripeException e) {
			e.printStackTrace();
			return "Error:" + e.getMessage();
		}
	}
}
