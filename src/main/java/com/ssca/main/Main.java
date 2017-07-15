package com.ssca.main;
import java.util.List;
import com.ssca.dex.*;
import com.ssca.format.Dex;

public class Main {

	public static void main(String[] args) {
//		args[0] is apk path
//		String apkPath = args[0];
		String apkPath = "/Users/gjy/Desktop/android/apks/diva-beta.apk";
		List<Dex> dexResult = DexParser.parseEachDexFile(apkPath);
	}

}
