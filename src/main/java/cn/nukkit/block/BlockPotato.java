package cn.nukkit.block;

import cn.nukkit.item.Item;
import cn.nukkit.item.ItemPotato;
import cn.nukkit.utils.Utils;

/**
 * Created by Pub4Game on 15.01.2016.
 */
public class BlockPotato extends BlockCrops {

    public BlockPotato(int meta) {
        super(meta);
    }

    public BlockPotato() {
        this(0);
    }

    @Override
    public String getName() {
        return "Potato Block";
    }

    @Override
    public int getId() {
        return POTATO_BLOCK;
    }

    @Override
    public Item toItem() {
        return new ItemPotato();
    }

    @Override
    public Item[] getDrops(Item item) {
        if (getDamage() >= 0x07) {
            return new Item[]{
                    new ItemPotato(0, Utils.random.nextInt(3) + 2)
            };
        } else {
            return new Item[]{
                    new ItemPotato()
            };
        }
    }
}
