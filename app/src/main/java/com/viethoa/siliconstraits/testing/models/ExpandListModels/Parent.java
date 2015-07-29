package com.viethoa.siliconstraits.testing.models.ExpandListModels;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by VietHoa on 22/04/15.
 */
public abstract class Parent<T> implements Serializable {

    public ArrayList<T> children;
}
