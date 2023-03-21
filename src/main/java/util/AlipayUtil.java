package util;

import com.alipay.easysdk.factory.Factory;
import com.alipay.easysdk.kernel.Config;

public class AlipayUtil {

	private static Config config;
	private static String AlipayCallbackUrl = "http://43.153.175.121:8080/SKRestaurant_Server/alipay";

	public static void InitAlipay() {
		if (config == null) {
			config = new Config();
			config.protocol = "https";
			config.gatewayHost = "openapi.alipay.com";
			config.signType = "RSA2";

			config.appId = "2021003184673480";

			config.merchantPrivateKey = "MIIEvQIBADANBgkqhkiG9w0BAQEFAASCBKcwggSjAgEAAoIBAQCCxKuXyyWkvHIbbmDHq/NK3squRdK12ZnZg0jblv1Yz3AzQPSoDUWwZxh8SVuhIlgWKI6WwV5wSQIDJUnjsUGVJk8c8PxsOoNO358IUdkj/9Dejz0+k5gtEGHp6r79N2N93Tyt3Q6pWfVJZWuaN1IRAVIwp1NJRzsSnlTtdMpFvY0HcLqeMIeYlZvhtHEtg1RaXCn9ZCQ4lseR9r+673YQyGW+DYzJKCEatrRwuxNDyio6ZClC7z9aDhuUsCqUexvkYUbur/3P63RrXmlvtp1iXae+9cE3J+6uGTn7l4cemYMdPnq4hmmbAHRD2KrrGV/SoVZ8WLBcAmpkCAMW1I2XAgMBAAECggEAGU0EBjJ/7gen7DbG8tsKD4pbnKxVwGmBotwL1LC6A9ze0IvYyksHGMJZsnJfECYc9OyT6gcvxnnZsdIdIbjB/6GR3P+jVy1bCn+pvWpYoOZ1+xmrt9fGsbFhwGhy23tD/K45d0o18/47vx+oWgYqErA2vsMhEiR+AXQw6mCWYjeqHZwGW5cM/hnuE6dUY2zIpai1nBq18L0I9BWXVrCjdAFitEWhhozHheeH5mwC9/7mdXVs9orXgYd6XkqkvECcIn+V2pSawhK3+aQ78c2d14F3leMK6xOf3Iw6kbCgOgEM2TS/V+XQWB6BegXaNbOrB8HqsF8CcjPaI4kIPBIVAQKBgQDhmCjPinLH2n9oJ+SWPnOXuZ3Bb2QzStlOHwzgdqyUM0mvM53J8dIc9/5HtTvG3mcaOIURJ76J5SJIGVpVxM4WQQF+VD6nWDEL3lWLwLSTXGTkBvLbPpiImY4blL6sMkgXsd1/d90khnhkJHJAAJzBZkW5NMwuFfYVIYfqzf6vcQKBgQCUZKiVzi5QCD5VsU3V3wxDSAuL1ECBagvGTpotMQqhz9SAzcf+CE8cJaWSaBOzrylFisHPqvEck3fHvHxfQE4LOnnxGYE4So4ctnVzxRqYss5N/OzBLwUVUdYURLP9wdBklx3xJRmEb3y5ilYE7cKKKjLdCAocLBQo2xE4UqQZhwKBgEU5OZylsR7eTyYYx63KsBGopV3L3oVWvGOaZ4shH0fCjSOzxQFIsN6uwloipJsbd6u2GC7xcShDJUfWb7/NyidN/zVSiH81Mqefq8Rcd4yYV2UZNMBN7uRLqi2QcQnJzBFpRLxufW9Ny/d+r4gwJ+m4hkCUkOq7Vm2ZgmlsmHAhAoGAU5p+bKAT8Z0jWW4iNxuKMiHV93qmuQLBDYHdVRarMc0AoWzOfy25PFdteBXQ+8v0yaxKUzCw1PO0aUClJZj6H1Nbvl0rM0a12DFn7MigmmUq3D9iN9jA2WkY3qRyb8YRHYsSIHlRbg7Ny07H9dDqaKMrsuHDqGmhFf+OLRhKQeMCgYEAtle4to+a0g93R8d6PoteqgusgXMHADG0POXrpUFEDGaxNLguAUZg2hYfXqv6ztxnG4EfUekomhDaH3vkljp9IR1rfi1VQ38X8oF/okwCJTCdRQAOVV4d7EzH9h7ng1xp3q73865ygwj3WlQUpLPDmv8X25j967wYgp+u6SHpNK8=";
			config.alipayPublicKey = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAn+k74raoydNkIrPevkQLzRn2Ed8g5MQE0nK1e5XSjx3sH8aB3xWVz9K4Akh0NK6j6cJfR2DJ8M979zQ0GBOwgEpS9rGNCEywvYGEoYRChc//9DwnqnuxjJ9SdF7xuuAMMj2wbcWQvpKcPeJtBLFZt63sIaVe76Um1Vs/MaKQwJrDyi1HNOLulfBDuyYytJZVKE2mJQ5hWL1fWcoJKaN9PNRue9VJQ8yIdpta/GPrLG8RzYEuQKUQBEPGK+wyZGi/+GrZWyc74xVYjbfC1yEFOX2Z1CukTtB/H/8MV2syIGRuTsia2HuN5JmuKoOY9TrY7q40RT6YVs3a6rdjj0mcowIDAQAB";

			// 可设置异步通知接收服务地址（可选）
			config.notifyUrl = AlipayCallbackUrl;
			
			Factory.setOptions(config);
		}
	}
}