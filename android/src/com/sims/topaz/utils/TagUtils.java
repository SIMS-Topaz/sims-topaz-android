package com.sims.topaz.utils;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.res.Resources;

import com.sims.topaz.R;
import com.sims.topaz.network.modele.Preview;
/**
 * Get the image to use for a certain tag
 * Helps us know if a string is a tag or not
 *
 */
public class TagUtils {
	
	public static void setTagUtils() {
		Resources mResources = SimsContext.getContext().getResources(); 
		TagUtils.TAG_SUN = mResources.getString(R.string.sun);
		TagUtils.TAG_RESTAURANT = mResources.getString(R.string.restaurant);
		TagUtils.TAG_PARTY = mResources.getString(R.string.party);
		TagUtils.TAG_PIZZA = mResources.getString(R.string.pizza);
		TagUtils.TAG_SHOPPING = mResources.getString(R.string.shopping);
		TagUtils.TAG_SCHOOL = mResources.getString(R.string.school);
		TagUtils.TAG_MUSIC = mResources.getString(R.string.music);
		TagUtils.TAG_HOSPITAL = mResources.getString(R.string.hospital);
		TagUtils.TAG_CAR = mResources.getString(R.string.car);
		TagUtils.TAG_CYCLE = mResources.getString(R.string.cycle);
		TagUtils.TAG = mResources.getString(R.string.tag);
	}

	  
	public static String TAG_SUN = "";
	public static String TAG_RESTAURANT = "";
	public static String TAG_PARTY = "";
	public static String TAG_PIZZA = "";
	public static String TAG_SHOPPING = "";
	public static String TAG_SCHOOL = "";
	public static String TAG_MUSIC = "";
	public static String TAG_HOSPITAL = "";
	public static String TAG_CAR = "";
	public static String TAG_CYCLE = "";
	public static String TAG = "";
	
	
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
	
	public static int getDrawableForPreview(Preview p){
		String finalTag=TagUtils.TAG;
		if(p.getTags()==null || p.getTags().isEmpty())//isEmpty requires API 9 
			return getDrawableForTag(TagUtils.TAG);
		for(String tag:getAllTags()){
			if(p.getTags().contains(tag)){
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
