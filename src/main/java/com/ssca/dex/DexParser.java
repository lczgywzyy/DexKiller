package com.ssca.dex;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.jar.JarFile;

import com.ssca.format.Dex;
import com.ssca.format.DexMethod;
import com.ssca.format.DexMethodInfo;
import com.ssca.format.Op_Format;
import com.ssca.utils.ApkUnZip;

public class DexParser {

	// public static List<String> allClassList = new ArrayList<>();

	public static List<Dex> parseEachDexFile(String apkPath) {
		List<Dex> res = new ArrayList<Dex>();

		JarFile jarFile = null;
		try {
			jarFile = new JarFile(apkPath);
		} catch (IOException e) {
			e.printStackTrace();
		}

		int dexCount = ApkUnZip.getDexCount(jarFile);
		for (int i = 1; i <= dexCount; i++) {
			String dexName;
			if (i == 1)
				dexName = "classes.dex";
			else
				dexName = "classes" + i + ".dex";
			try {
				System.out.println("start parse " + dexName);
				Dex thisDex = new Dex(dexName);
				Op_Format.init_ops(thisDex.ops);
				DexHeaderParser.getHeaderInfo(jarFile, dexName, thisDex);
				DexStringParser.getStringInfo(jarFile, dexName, thisDex);
				DexTypeParser.getTypeInfo(jarFile, dexName, thisDex);
				DexClassParser.getClassInfo(jarFile, dexName, thisDex);
				DexProtoParser.getProtoInfo(jarFile, dexName, thisDex);
				DexMethodParser.getMethodInfo(jarFile, dexName, thisDex);
//				for(String classname: thisDex.classDataList.keySet()) {
//					DexClassInvokeParser.getClassInvoke(jarFile, dexName, thisDex, classname);
//				}
				DexClassInvokeParser.getClassInvoke(jarFile, dexName, thisDex, "Ljakhar/aseem/diva/HardcodeActivity;");
				System.out.println("Parsing class :" + "Ljakhar/aseem/diva/HardcodeActivity;");
				System.out.println("");
				for(DexMethodInfo method : thisDex.classDataList.get("Ljakhar/aseem/diva/HardcodeActivity;").methodlist) {
//					System.out.println(method.name);
					DexMethodInvokeParser.getMethodInvoke(jarFile, dexName, thisDex, method);
				}
				res.add(thisDex);
			} catch (Exception e) {
				e.printStackTrace();
			}

		}
		// System.out.println("all classes count: "+allClassList.size());
		return res;
	}

	// public static void main(String[] args) {
	// String s = "/Users/konghaohao/Desktop/test_result/APK/baidumap.apk";
	// long starTime = System.currentTimeMillis();
	// List<Dex> dexResult = DexParser.parseEachDexFile(s);
	// long endTime = System.currentTimeMillis();
	// System.out.println("耗时：" + (endTime - starTime) + " ms");
	// }

	/**
	 * @param apkPath
	 *            - apk路径.
	 * @return 返回调用方法(非声明方法)列表.
	 */
	public static List<DexMethod> getReferedListFromApk(String apkPath) {
		List<DexMethod> methodDefinedList = new ArrayList<DexMethod>();
		List<DexMethod> methodReferedList = new ArrayList<DexMethod>();
		Set<DexMethod> methodSet = new HashSet<DexMethod>();
		Set<String> classSet = new HashSet<String>();
		List<Dex> dexList = parseEachDexFile(new File(apkPath).getAbsolutePath());
		for (Dex dex : dexList) {
			methodSet.addAll(dex.methodList);
			classSet.addAll(dex.classList);
		}
		updateMethodInfo(methodSet, classSet, methodDefinedList, methodReferedList);
		return methodReferedList;
	}

	/**
	 * @param apkPath
	 *            - apk路径.
	 * @return 返回类列表.
	 */
	public static List<String> getClassListFromApk(String apkPath) {
		Set<String> classSet = new HashSet<String>();
		List<String> classList = new ArrayList<String>();
		List<Dex> dexList = parseEachDexFile(new File(apkPath).getAbsolutePath());
		for (Dex dex : dexList) {
			classSet.addAll(dex.classList);
		}
		classList.addAll(classSet);
		return classList;
	}

	public static void updateMethodInfo(Set<DexMethod> methodSet, Set<String> classSet,
			List<DexMethod> methodDefinedList, List<DexMethod> methodReferedList) {
		if (!methodSet.isEmpty()) {
			for (DexMethod dexMethod : methodSet) {
				if (classSet.contains(dexMethod.classType)) {
					methodDefinedList.add(dexMethod);
				} else {
					methodReferedList.add(dexMethod);
				}
			}
		}
	}
}
