package com.zhj.safeguard;

import android.test.AndroidTestCase;

import com.zhj.safeguard.DB.BlackDao;
import com.zhj.safeguard.bean.BlackBean;

import java.util.List;

/**
 * Created by hasee on 2016/9/2.
 * 测试黑名单的工具类.
 */
public class TestBlackDao extends AndroidTestCase {

    public void testAdd() {
        BlackDao dao = new BlackDao(getContext());

        boolean add = dao.add("55555", 0);
        assertEquals(true, add);
    }

    public void testAddList() {
        BlackDao dao = new BlackDao(getContext());

        for (int i = 0; i < 100; i++) {
            boolean add = dao.add("13513239874" + i, 0);
        }
    }

    public void testUpdate() {
        BlackDao dao = new BlackDao(getContext());

        boolean update = dao.update("55555", 1);
        assertEquals(true, update);
    }

    public void testFind() {
        BlackDao dao = new BlackDao(getContext());

        List<BlackBean> list = dao.findAll();
        for (BlackBean bean : list) {
            assertEquals("55555", bean.number);
            assertEquals(1, bean.type);
        }

    }

    public void testDelete() {
        BlackDao dao = new BlackDao(getContext());

        boolean delete = dao.delete("55555");
        assertEquals(true, delete);
    }
}
