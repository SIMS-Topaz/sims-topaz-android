package com.sims.topaz.utils;

import java.util.ArrayList;
import java.util.List;

import com.sims.topaz.R;
/**
 * Get the image to use for a certain tag
 * Helps us know if a string is a tag or not
 *
 */
public class TagUtils {
	
	public static final String TAG_SUN="sun";
	public static final String TAG_RESTAURANT="restaurant";
	public static final String TAG_PARTY="party";
	public static final String TAG_PIZZA="pizza";
	public static final String TAG_SHOPPING="shopping";
	public static final String TAG_SCHOOL="school";
	public static final String TAG_MUSIC="music";
	public static final String TAG_HOSPITAL="hospital";
	public static final String TAG_CAR="car";
	public static final String TAG_CYCLE="cycle";
	public static final String TAG="tag";
	
	
	public static List<String> getAllTags(){
		List<String> tagList = new ArrayList<String>();
		tagList.add(TagUtils.TAG_RESTAURANT);
		tagList.add(TagUtils.TAG_PARTY);
		tagList.add(TagUtils.TAG_PIZZA);
		tagList.add(TagUtils.TAG_SHOPPING);
		tagList.add(TagUtils.TAG_SCHOOL);
		tagList.add(TagUtils.TAG_MUSIC);
		tagList.add(TagUtils.TAG_HOSPITAL);
		tagList.add(TagUtils.TAG_SUN);
		tagList.add(TagUtils.TAG_CAR);
		tagList.add(TagUtils.TAG_CYCLE);
		return tagList;
	}
	
	public static int getDrawableForString(String text){
		String finalTag=TagUtils.TAG;
		if(text==null || text.equals(""))//isEmpty requires API 9 
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
    	if(tag.equals(TagUtils.TAG_MUSIC))
    		return R.drawable.tag_music;
    	if(tag.equals(TagUtils.TAG_SUN))
    		return R.drawable.tag_sun;
    	if(tag.equals(TagUtils.TAG_CAR))
    		return R.drawable.tag_car;
    	if(tag.equals(TagUtils.TAG_CYCLE))
    		return R.drawable.tag_cycle;
    	return R.drawable.tag;
    }
}
