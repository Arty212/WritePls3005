package com.brks.writepls.Compare;

import com.brks.writepls.ShoppingList.ShoppingElement;

import java.util.Comparator;

public class CompareShoppingElement implements Comparator<ShoppingElement> {
    @Override
    public int compare(ShoppingElement o1, ShoppingElement o2) {


        return Boolean.compare(o2.isStatus(),o1.isStatus());
    }
}
