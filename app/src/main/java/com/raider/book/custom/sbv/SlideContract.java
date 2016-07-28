package com.raider.book.custom.sbv;

import com.raider.book.BaseView;

import java.nio.MappedByteBuffer;

public interface SlideContract {

    interface View extends BaseView<SlidePresenter> {
        void setMBB(MappedByteBuffer mbb);
    }

    interface Model {
        MappedByteBuffer getMBB(String path);
    }

}
