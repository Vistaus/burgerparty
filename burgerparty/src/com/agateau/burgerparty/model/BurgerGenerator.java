package com.agateau.burgerparty.model;

import java.util.Collection;
import java.util.Vector;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Array;

public class BurgerGenerator {
    private Array<TopBottom> mTopBottomItems = new Array<TopBottom>();
    private Array<BurgerItem> mMiddleItems = new Array<BurgerItem>();
    private Array<BurgerItem> mMainItems = new Array<BurgerItem>();

    public BurgerGenerator(int worldIndex, Array<BurgerItem> items) {
        for (BurgerItem item: items) {
            switch (item.getSubType()) {
            case MIDDLE_MAIN:
                mMainItems.add(item);
                mMiddleItems.add(item);
                break;
            case MIDDLE_OTHER:
            case MIDDLE_SAUCE:
                mMiddleItems.add(item);
                break;
            case BOTTOM:
                // Skip, assume there are matching tops
                break;
            case TOP:
                BurgerItem bottomItem = (BurgerItem)MealItemDb.getInstance().get(worldIndex, item.getBottomName());
                assert bottomItem != null;
                mTopBottomItems.add(new TopBottom(item, bottomItem));
                break;
            case TOP_BOTTOM:
                mTopBottomItems.add(new TopBottom(item, item));
                break;
            }
        }
    }

    public Collection<BurgerItem> run(int count) {
        Vector<BurgerItem> lst = new Vector<BurgerItem>();
        lst.setSize(count);
        TopBottom topBottom = mTopBottomItems.get(MathUtils.random(mTopBottomItems.size - 1));
        lst.set(0, topBottom.bottom);
        lst.set(count - 1, topBottom.top);
        // Create a second stage for tall burgers
        if (count >= 7) {
            int separator = count / 2;
            lst.set(separator, topBottom.bottom);
            fillStage(lst, 1, separator);
            fillStage(lst, separator + 1, count - 1);
        } else {
            fillStage(lst, 1, count - 1);
        }
        return lst;
    }

    public void fillStage(Vector<BurgerItem> lst, int start, int end) {
        // Generate content, make sure items cannot appear two times consecutively
        Array<BurgerItem> items = new Array<BurgerItem>(mMiddleItems);
        BurgerItem lastItem = null;

        boolean hasMain = false;
        for (int pos = start; pos < end; ++pos) {
            BurgerItem item = null;
            int index = -1;
            while (index == -1) {
                index = MathUtils.random(items.size - 1);
                item = items.get(index);
                if (lastItem != null) {
                    if (lastItem.getSubType() == BurgerItem.SubType.MIDDLE_SAUCE && item.getSubType() == BurgerItem.SubType.MIDDLE_SAUCE) {
                        // Try again, we don't want two consecutive sauces
                        index = -1;
                    }
                }
            }
            items.removeIndex(index);
            lst.set(pos, item);
            if (item.getSubType() == BurgerItem.SubType.MIDDLE_MAIN) {
                hasMain = true;
            }
            if (lastItem != null) {
                items.add(lastItem);
            }
            lastItem = item;
        }

        // Make sure we have at least one main item
        if (!hasMain) {
            BurgerItem item = mMainItems.get(MathUtils.random(mMainItems.size - 1));
            int pos = MathUtils.random(start, end - 1);
            lst.set(pos, item);
        }
    }

    private static class TopBottom {
        public BurgerItem top;
        public BurgerItem bottom;
        public TopBottom(BurgerItem t, BurgerItem b) {
            top = t;
            bottom = b;
        }
    }
}