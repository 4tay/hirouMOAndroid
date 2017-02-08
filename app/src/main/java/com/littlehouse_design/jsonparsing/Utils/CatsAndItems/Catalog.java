package com.littlehouse_design.jsonparsing.Utils.CatsAndItems;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

/**
 * Created by johnkonderla on 12/23/16.
 */

public class Catalog implements Parcelable {
    private String code;
    private String parentCode;
    private String name;
    private ArrayList<Catalog> children;
    private int level;
    private ArrayList<String> childImages;
    private ArrayList<String> childNames;
    private ArrayList<String> childCodes;
    private ArrayList<Boolean> skipCategory;

    public Catalog(String code, String parentCode, String name, int level) {
        this.code = code;
        this.name = name;
        this.parentCode = parentCode;
        this.level = level;
        children = new ArrayList<>();
    }
    public Catalog(String code, String parentCode, String name) {
        this.code = code;
        this.name = name;
        this.parentCode = parentCode;
        //this.level = level;
        children = new ArrayList<>();
    }
    public Catalog(String code, String parentCode, String name, ArrayList<Catalog> children, int level) {
        this.code = code;
        this.parentCode = parentCode;
        this.name = name;
        this.children = children;
        this.level = level;
    }
    public Catalog() {
        children = new ArrayList<>();
    }

    public String getCode() {
        return code;
    }

    public String getParentCode() {
        return parentCode;
    }

    public String getName() {
        return name;
    }

    public ArrayList<Catalog> getChildren() {
        return children;
    }

    public Catalog getChild(int i) {
        return children.get(i);
    }
    public int getLevel() {return level;}

    public ArrayList<String> getChildCodes() {
        return childCodes;
    }

    public ArrayList<String> getChildNames() {
        return childNames;
    }
    public ArrayList<String> getChildImages() {return childImages;}

    public void setChildren(ArrayList<Catalog> children) {
        this.children = children;
    }
    public void addToArray(int i, Catalog cat) {
        children.add(i,cat);
    }

    public void setCode(String code) {
        this.code = code;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ArrayList<Boolean> getSkipCategory() {
        return skipCategory;
    }

    public void setParentCode(String parentCode) {
        this.parentCode = parentCode;
    }

    public void setLevel(int level) {
        this.level = level;
    }
    public void setChildCodes(ArrayList<String> childCodes) {
        this.childCodes = childCodes;
    }
    public void setChildNames(ArrayList<String> childNames) {
        this.childNames = childNames;
    }

    public void setChildImages(ArrayList<String> childImages) {
        this.childImages = childImages;
    }

    protected Catalog(Parcel in) {
        this.code = in.readString();
        this.parentCode = in.readString();
        this.name = in.readString();
    }

    public void setSkipCategory(ArrayList<Boolean> skipCategory) {
        this.skipCategory = skipCategory;
    }

    @Override
    public int describeContents() {
        return 0;
    }
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(code);
        dest.writeString(parentCode);
        dest.writeString(name);
    }
    public static final Creator<Catalog> CREATOR = new Creator<Catalog>() {
        @Override
        public Catalog createFromParcel(Parcel in) {
            return new Catalog(in);
        }

        @Override
        public Catalog[] newArray(int size) {
            return new Catalog[size];
        }
    };
}
