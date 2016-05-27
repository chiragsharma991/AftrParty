package com.aperotechnologies.aftrparties.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by mpatil on 24/05/16.
 */
public class LIPictureData
{
    String _total = "0";
    List<String> values =null;

    public LIPictureData()
    {

    }

    public String get_total()
    {
        return _total;
    }

    public void set_total(String _total)
    {
        this._total = _total;
    }


    public List<String> getvalues()
    {
        return values;
    }

    public void setvalues(ArrayList<String> values)
    {
        this.values = values;
    }


}
