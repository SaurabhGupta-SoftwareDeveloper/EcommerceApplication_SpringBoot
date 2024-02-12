package com.saurabh.myshop.helper;

import java.io.File;

public class AdminPassword {
public static void main(String[] args) {
//	System.out.println(AES.encrypt("admin", "123"));
	File file= new File("src/main/resources/static/images");
	if(!file.isDirectory()) {
		file.mkdir();
	}
}
}
