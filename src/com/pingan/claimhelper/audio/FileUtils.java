package com.pingan.claimhelper.audio;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import android.os.Environment;

/**
 * 文件操作类
 * @author qingzheng.xin
 *
 */
public class FileUtils {
	private String SDPATH;

	public String getSDPATH() {
		return SDPATH;
	}

	public FileUtils() {
		// 得到当前外部存储设备的目录
		// /SDCARD
		SDPATH = Environment.getExternalStorageDirectory() + "/";
	}

	/**
	 * 在SD卡上创建文件
	 * 
	 * @throws IOException
	 */
	public File creatSDFile(String fileName) throws IOException {
		File file = new File(SDPATH + fileName);
		file.createNewFile();
		return file;
	}

	/**
	 * 在SD卡上创建目录
	 * 
	 * @param dirName
	 */
	public File creatSDDir(String dirName) {
		File dir = new File(SDPATH + dirName);
		dir.mkdirs();
		return dir;
	}

	/**
	 * 判断SD卡上的文件夹是否存在
	 */
	public boolean isFileExist(String fileName) {
		File file = new File(SDPATH + fileName);
		return file.exists();
	}

	/**
	 * 将一个InputStream里面的数据写入到SD卡中
	 */
	public File write2SDFromInput(String path, String fileName,
			InputStream input) {
		File file = null;
		OutputStream output = null;
		try {
			creatSDDir(path);
			file = creatSDFile(path + fileName);
			output = new FileOutputStream(file);
			byte buffer[] = new byte[4 * 1024];
			while ((input.read(buffer)) != -1) {
				output.write(buffer);
			}
			output.flush();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				output.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return file;
	}

	/**
	 * 删除文件夹
	 * 
	 * @param file
	 */
	public static void deleSDFolder(File file) {
		if (file.exists()) {
			if (file.listFiles().length > 0) {
				for (File temp : file.listFiles()) {
					if (temp.isDirectory()) {
						deleSDFolder(temp);
					} else {
						temp.delete();
					}
				}
			}
			file.delete();
		}
		try {
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 删除文件
	 * 
	 * @param file
	 */
	public static void deleSDFile(File file) {

		try {
			if (file.exists() && file.isFile()) {
				file.delete();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/*
	 * 获取文件列表
	 */
	public static List<String> getFileList(String path) {
		List<String> list = new ArrayList<String>();
		File file = new File(path);
		File[] files;
		files = file.listFiles();
		int count = files.length;
		for (int i = 0; i < count; i++) {
			File f = files[i];
			list.add(f.getPath());
		}
		return list;
	}

	/*
	 * 获取文件类型
	 */
	public static String getFileType(String filename) {
		String type = "";
		String end = filename.substring(filename.lastIndexOf(".") + 1)
				.toLowerCase();
		if (end.equals("jpg")) {
			type = "image";
		} else if (end.equals("3gp") || end.equals("mp4")) {
			type = "video";
		} else if (end.equals("amr") || end.equals("mp3")) {
			type = "audio";
		} else {
			type = "*";
		}
		type += "/*";
		return type;
	}

}