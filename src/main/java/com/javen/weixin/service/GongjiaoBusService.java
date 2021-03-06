package com.javen.weixin.service;

import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import com.javen.weixin.entity.GongjiaoBus;
import com.javen.weixin.entity.Segment;
import com.jfinal.weixin.sdk.utils.HttpUtils;

/**
 * @author Javen
 * @Email javenlife@126.com
 * 公交驾乘查询  公交换乘查询，该接口根据起点和终点信息查询公交换乘方案。
 */
public class GongjiaoBusService {
	private static List<GongjiaoBus> getGongjiaoBus(String city,String start_addr,String end_addr){
		String requestUrl = "http://openapi.aibang.com/bus/transfer?app_key=0a64bf2d8ee2248700e8bc32e6aaa3fe&city={city}&start_addr={start_addr}&end_addr={end_addr}";
	    // 对城市和线路进行编码
	    requestUrl = requestUrl.replace("{city}", urlEncodeUTF8(city));
	    requestUrl = requestUrl.replace("{start_addr}", urlEncodeUTF8(start_addr));
	    requestUrl = requestUrl.replace("{end_addr}", urlEncodeUTF8(end_addr));
	    // 处理名称、作者中间的空格
	    requestUrl = requestUrl.replaceAll("\\+", "%20");
	    System.out.println(requestUrl);
	    InputStream inputStream=HttpUtils.download(requestUrl, null);
//	    InputStream inputStream=HttpUtil.getInputStreamByGet(requestUrl);
	    if (inputStream!=null) {
	    	return parseGongjiaoBus(inputStream);
		}
		return null;
		
	}
	private static List<GongjiaoBus> parseGongjiaoBus(InputStream inputStream) {
		List<GongjiaoBus> gongjiaoBus=new ArrayList<GongjiaoBus>();
	  try {
		  // 使用dom4j解析xml字符串
		  SAXReader reader = new SAXReader();
		  Document document = reader.read(inputStream);
		  // 得到xml根元素
		  Element root = document.getRootElement();
		  // result_num表示查询得公交路线数量
		  String num = root.element("result_num").getText();
		  if (!"0".equals(num)) {
			  List<Element> buses = root.elements("buses");
			  List<Element> bus = buses.get(0).elements("bus");
		      for (int i = 0; i < bus.size(); i++) {
		    	 Element item=bus.get(i);
		    	 String dist=item.elementText("dist");
		    	 String time=item.elementText("time");
		    	 String foot_dist=item.elementText("foot_dist");
		    	 String last_foot_dist=item.elementText("last_foot_dist");
		    	 //乘车 考虑到换成
		    	 Element segments =item.element("segments");
		    	 List<Element> segment = segments.elements("segment");
		    	 List<Segment> list=new ArrayList<Segment>();
		    	 for (Element element : segment) {
					String line_name=element.elementText("line_name");
					String stats=element.elementText("stats");
					String foot_dist2=element.elementText("foot_dist");
					list.add(new Segment(line_name, stats, foot_dist2));
				}
		    	 gongjiaoBus.add(new GongjiaoBus(dist, time, foot_dist, last_foot_dist, list)); 
			}
		      
		      return gongjiaoBus;
		  }
	} catch (DocumentException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
	  return null;
	}
	
	
	public static String getgetGongjiaoBusSer(String city,String start_addr,String end_addr){
		List<GongjiaoBus> bus= getGongjiaoBus(city, start_addr, end_addr);
		StringBuffer buffer=new StringBuffer();
		if (bus!=null) {
			for (int i = 0; i < 5; i++) {
				buffer.append("\ue132 方案"+(i+1)+"\n");
				buffer.append("总距离："+bus.get(i).getDist()+"m 时间："+bus.get(i).getTime()+"分钟 ");
				String foot=bus.get(i).getFoot_dist();
				if (!foot.equals("0")) {
					buffer.append("步行距离："+bus.get(i).getFoot_dist()+"m \n\n");
				}else {
					buffer.append("\n\n");
				}
				String last_foot_dist=bus.get(i).getLast_foot_dist();
				if (!last_foot_dist.equals("0")) {
					buffer.append("\ue231 先步行"+last_foot_dist+"m到公交站\n");
				}
				List<Segment> list=bus.get(i).getList();
				for (int j = 0; j < list.size(); j++) {
					String ch="乘";
					if (j>0) {
						ch="换乘";
					}
					buffer.append("\ue231"+ch+list.get(j).getLine_name().substring(0,list.get(j).getLine_name().indexOf("("))+",途径站点:"+list.get(j).getStats()+"\n");
					String foot_dist=list.get(j).getFoot_dist();
					if (!foot_dist.equals("0")) {
						if (list.size()>j+1 && list.get(j+1).getLine_name()!=null) {
							buffer.append("\ue231 再步行"+foot_dist+"m到达"+list.get(j+1).getLine_name());
						}else {
							buffer.append("\ue231 再步行"+foot_dist+"m到达"+end_addr);
						}
					}
					buffer.append("\n\n");
				}
				
			}
			return buffer.toString();
		}
		return null;
		
	}
	
	/**
	    * UTF-8编码
	    *
	    * @param source
	    * @return
	    */
	  public static String urlEncodeUTF8(String source) {
	    String result = source;
	    try {
	      result = java.net.URLEncoder.encode(source, "UTF-8");
	    } catch (UnsupportedEncodingException e) {
	      e.printStackTrace();
	    }
	    return result;
	  }
	
	public static void main(String[] args) {
		List<GongjiaoBus> list = getGongjiaoBus("深圳", "油松派出所", "深圳北站");
		for (GongjiaoBus gongjiaoBus : list) {
			System.out.println(gongjiaoBus.toString());
		}
	}
}
