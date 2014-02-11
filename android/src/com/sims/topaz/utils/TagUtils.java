package com.sims.topaz.utils;

import java.util.ArrayList;
import java.util.List;

import com.sims.topaz.R;

public class TagUtils {
	
	public static final String TAG_SUN="tag_sun";
	public static final String TAG_RESTAURANT="tag_restaurant";
	public static final String TAG_PARTY="tag_party";
	public static final String TAG_PIZZA="tag_pizza";
	public static final String TAG_SHOPPING="tag_shopping";
	public static final String TAG_SCHOOL="tag_school";
	public static final String TAG_TOILETS="tag_toilet";
	public static final String TAG_HOSPITAL="tag_hospital";
	public static final String TAG="tag";
	
	
	public static List<String> getAllTags(){
		List<String> tagList = new ArrayList<String>();
		tagList.add(TagUtils.TAG_RESTAURANT);
		tagList.add(TagUtils.TAG_PARTY);
		tagList.add(TagUtils.TAG_PIZZA);
		tagList.add(TagUtils.TAG_SHOPPING);
		tagList.add(TagUtils.TAG_SCHOOL);
		tagList.add(TagUtils.TAG_TOILETS);
		tagList.add(TagUtils.TAG_HOSPITAL);
		tagList.add(TagUtils.TAG_SUN);
		return tagList;
	}
	
	public static int getDrawableForString(String text){
		String finalTag=TagUtils.TAG;
		if(text==null || text.isEmpty()) 
			return getDrawableForTag(TagUtils.TAG);
		for(String tag:getAllTags()){
			if(text.contains(tag) || tag.contains(text)){
				finalTag = tag;
				break;
			}
		}
		return getDrawableForTag(finalTag);
	}
	
    public static int getDrawableForTag(String tag){
    	if(tag.equals(TagUtils.TAG_PARTY))
    		return R.drawable.tag_party;
    	if(tag.equals(TagUtils.TAG_HOSPITAL))
    		return R.drawable.tag_hospital;
    	if(tag.equals(TagUtils.TAG_PIZZA))
    		return R.drawable.tag_pizza;
    	if(tag.equals(TagUtils.TAG_RESTAURANT))
    		return R.drawable.tag_restaurant;
    	if(tag.equals(TagUtils.TAG_SCHOOL))
    		return R.drawable.tag_school;
    	if(tag.equals(TagUtils.TAG_SHOPPING))
    		return R.drawable.tag_shopping;
    	if(tag.equals(TagUtils.TAG_TOILETS))
    		return R.drawable.tag_toilet;
    	if(tag.equals(TagUtils.TAG_SUN))
    		return R.drawable.tag_sun;
    	return R.drawable.tag;
    }
}
