package com.javen.weixin.service;

import java.util.ArrayList;
import java.util.List;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.javen.weixin.entity.BaiduWeatherEntity;
import com.jfinal.weixin.sdk.utils.HttpUtils;

/**
 * @author Javen
 * @Email javenlife@126.com
 * 
 */
public class BaiduWeatherService {
	
	private static List<BaiduWeatherEntity> getBaiduWeather(String location){
		String ak="w5CHR6GMqwCkcTx8l4DqTWls";
		String url="http://api.map.baidu.com/telematics/v3/weather?" +
		"location={location}" +
		"&output=json" +
		"&ak="+ak;
		List<BaiduWeatherEntity> list=new ArrayList<BaiduWeatherEntity>();
		url=url.replace("{location}", location);
		String jsonResult= HttpUtils.get(url);
		System.out.println(jsonResult);
		JSONObject jsonObject=JSON.parseObject(jsonResult);
		int error=jsonObject.getInteger("error");
		String status=jsonObject.getString("status");
		if (error==0 && status.equalsIgnoreCase("success")) {
			String date=jsonObject.getString("date");
			
			JSONArray results=jsonObject.getJSONArray("results");
			
			JSONObject resultObject=results.getJSONObject(0);
			String currentCity=resultObject.getString("currentCity");
			String pm25=resultObject.getString("pm25");
			JSONArray weather_data=resultObject.getJSONArray("weather_data");
			JSONArray weather_index=resultObject.getJSONArray("index");
			String other="";
			StringBuffer sbf=new StringBuffer();
			for (int i = 0; i < weather_index.size(); i++) {
				JSONObject index=weather_index.getJSONObject(i);
				String zs=index.getString("zs");
				String tipt=index.getString("tipt");
				String des=index.getString("des");
				sbf.append(tipt+":"+zs+"\n" +des +"\n\n");
			}
			other=sbf.toString();
			for (int i = 0; i < weather_data.size(); i++) {
				JSONObject weather=weather_data.getJSONObject(i);
				String resdate=weather.getString("date");
				String dayPictureUrl=weather.getString("dayPictureUrl");
				String nightPictureUrl=weather.getString("nightPictureUrl");
				String resweather=weather.getString("weather");
				String wind=weather.getString("wind");
				String temperature=weather.getString("temperature");
				
				list.add(new BaiduWeatherEntity(currentCity,date, resdate, dayPictureUrl, nightPictureUrl, resweather, wind, temperature,pm25,other));
			}
		}
		return list;
	}
	
	public static String getWeatherService(String location){
		List<BaiduWeatherEntity> entities= getBaiduWeather(location);
		StringBuffer buffer = new StringBuffer();
		if (entities.size()>0) {
			buffer.append(entities.get(0).getCurrentCity()+"  天气如下:\n\n");
			buffer.append("pm25:"+entities.get(0).getPm25()+"\n\n"+entities.get(0).getOther());
			for (BaiduWeatherEntity baiduWeatherEntity : entities) {
				buffer.append(baiduWeatherEntity.getResDate()+"\n");
				buffer.append("天气情况:"+baiduWeatherEntity.getWeather()+"\n");
				buffer.append("风力:"+baiduWeatherEntity.getWind()+"\n");
				buffer.append("温度:"+baiduWeatherEntity.getTemperature()+"\n\n");
				
			}
			buffer.append("更新时间："+entities.get(0).getDate());
		}else {
			return "\ue252 木有找到对应的天气，请检查城市是否输入正确！！";
		}
		return buffer.toString();
	}
	
	public static String getGuide(){
		StringBuffer buffer = new StringBuffer();
        buffer.append("\ue44a 天气查询操作指南").append("\n\n");  
        buffer.append("1、天气@城市").append("\n");  
        buffer.append("2、点击对话框下面左边的键盘，切换输入状态，点击+，发送位置即可！").append("\n");  
        buffer.append("回复“?”显示主菜单");  
        return buffer.toString();
	}
	
	public static void main(String[] args) {
		System.out.println(getWeatherService("深圳"));
	}
}
