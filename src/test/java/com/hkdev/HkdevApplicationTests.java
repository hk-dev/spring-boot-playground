package com.hkdev;

import com.hkdev.web.i18n.I18NService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.assertEquals;

@RunWith(SpringRunner.class)
@SpringBootTest
public class HkdevApplicationTests {

	@Autowired
	private I18NService i18NService;

	@Test
	public void testMessageByLocalService() throws Exception {
		String expected = "Bootstrap starter template";
		String messageId = "index.main.title";
		String actual = i18NService.getMessage(messageId);
		assertEquals(expected, actual);
	}
}
