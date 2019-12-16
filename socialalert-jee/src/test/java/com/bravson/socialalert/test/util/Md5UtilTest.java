package com.bravson.socialalert.test.util;

import java.io.File;
import java.io.IOException;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import com.bravson.socialalert.infrastructure.util.Md5Util;

public class Md5UtilTest extends Assertions {
	
	@Test
	public void computeMd5() throws IOException {
		String result = Md5Util.computeMd5Hex(new File("src/main/resources/logo.jpg"));
		assertThat(result).isEqualTo("38c4297b9099b466eab20fea521ee2f6");
	}
}
