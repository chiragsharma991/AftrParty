package com.aperotechnologies.aftrparties.model;

/**
 * Created by hasai on 27/08/16.
 */
public class FriendsSelectedList {

    String fbid = null;
    String fbname = null;
    String type = null;
    boolean selected = false;

    public FriendsSelectedList(String fbid, String fbname, String type, boolean selected)
    {
        super();
        this.fbid = fbid;
        this.fbname = fbname;
        this.type = type;
        this.selected = selected;
    }

    public String getId()
    {
        return fbid;
    }
    public void setId(String fbid)
    {
        this.fbid = fbid;
    }

    public String getFbname()
    {
        return fbname;
    }
    public void setFbname(String fbname)
    {
        this.fbname = fbname;
    }

    public String getType()
    {
        return type;
    }
    public void setType(String type)
    {
        this.type = type;
    }

    public boolean isSelected()
    {
        return selected;
    }
    public void setSelected(boolean selected)
    {
        this.selected = selected;
    }
    public void toggleChecked() {
        selected = !selected ;
    }
}