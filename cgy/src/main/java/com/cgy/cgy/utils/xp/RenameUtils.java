package com.cgy.cgy.utils.xp;

import java.io.File;
import java.util.HashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RenameUtils {

	private static Logger log = LoggerFactory.getLogger(RenameUtils.class);

	// 测试导入
	public static void main(String[] args) {
//		replaceAll("C:/Users/Xpeng/Desktop/d55仪表ui/导航地图/resource/lane车道地图端", "vector_ic_mid_normal", "navi_lane");
//		replaceAll("C:/Users/Xpeng/Desktop/d55仪表ui/导航地图/resource/lane分道图标", "_night", "");
//		replaceAll("C:/Users/Xpeng/Desktop/d55仪表ui/导航地图/resource/导航地图最终版/resource/白天/车道线指示", "车道线指示navi_lane",
//				"navi_lane");
//		replaceAll("C:/Users/Xpeng/Desktop/d55仪表ui/导航地图/resource/导航地图最终版/resource/白天/车道线指示", "_night", "");
//		replaceAll("C:/Users/Xpeng/Desktop/d55仪表ui/导航地图/resource/转向图片仪表/白天指示-大", "png_ic_xlarge_Normal_ic_sou", "navi_maneuver_ic_");

		compareFileNames("C:/Users/Xpeng/Desktop/d55仪表ui/导航地图/resource/导航地图最终版/resource/白天/车道线指示",
				"C:/Users/Xpeng/Desktop/d55仪表ui/导航地图/resource/lane车道地图端");
		compareFileNames("C:/Users/Xpeng/Desktop/d55仪表ui/导航地图/resource/lane车道地图端",
				"C:/Users/Xpeng/Desktop/d55仪表ui/导航地图/resource/导航地图最终版/resource/白天/车道线指示");
	}


	public static void compareFileNames(String dirPathSource, String dirPathTarget) {
		File sourceDir = new File(dirPathSource);
		File targetDir = new File(dirPathTarget);

		if (sourceDir.isDirectory() && targetDir.isDirectory()) {
			HashMap<String, String> fileNameMap = new HashMap<>();
			for (File file : sourceDir.listFiles()) {
				String fileNameString = file.getName().toLowerCase().split("\\.")[0];
				fileNameMap.put(fileNameString, file.getPath());
			}
			log.info("sourceDir {} 下文件数目 :{}", dirPathSource, fileNameMap.size());

			int notFoundCount = 0;
			for (File file : targetDir.listFiles()) {
				String targetFileNameString = file.getName().toLowerCase().split("\\.")[0];
				if (!fileNameMap.containsKey(targetFileNameString)) {
					log.info(" {} 下没有文件 :{}", sourceDir.getName(), targetFileNameString);
					notFoundCount++;
				}
			}

			log.info(" {} 下共有{}个文件没找到", sourceDir.getName(), notFoundCount);

		}

	}

	public static void replaceAll(String dirPath, String nameNeedReplace, String replaceMent) {
		if (dirPath == null || dirPath.length() == 0) {
			return;
		}

		log.info("批量重命名开始，dirPath:{}", dirPath);
		File dirFile = new File(dirPath);
		if (dirFile.exists() && dirFile.isDirectory()) {
			for (File itemFile : dirFile.listFiles()) {
				if (!itemFile.isDirectory()) {
					renameFile(itemFile, nameNeedReplace, replaceMent);
				}
			}

		}

		log.info("批量重命名完成，dirPath:{}", dirPath);
	}

	/**
	 * 重命名文件
	 * 
	 * @param file
	 * @param nameNeedReplace
	 * @param replaceMent
	 */
	public static void renameFile(File file, String nameNeedReplace, String replaceMent) {
		String filePath = file.getPath();
		if (filePath.contains(nameNeedReplace)) {
			String newFileNameString = file.getName().replace(nameNeedReplace, replaceMent).toLowerCase();

			File destFile = new File(file.getParent() + "/" + newFileNameString);

//			log.info("文件需要重命名，原名:{}，修改后的名字：{}", file.getName(), destFile.getName());
			if (!file.renameTo(destFile)) {
				log.info("重命名失败，原名:{} ", file.getName());
			}
		}
	}

}
