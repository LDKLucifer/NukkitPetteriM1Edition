package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.blockentity.BlockEntity;
import cn.nukkit.blockentity.BlockEntityFlowerPot;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemFlowerPot;
import cn.nukkit.level.Level;
import cn.nukkit.math.AxisAlignedBB;
import cn.nukkit.math.BlockFace;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.nbt.tag.Tag;

/**
 * @author Nukkit Project Team
 */
public class BlockFlowerPot extends BlockFlowable {

    public BlockFlowerPot() {
        this(0);
    }

    public BlockFlowerPot(int meta) {
        super(meta);
    }

    protected static boolean canPlaceIntoFlowerPot(int id) {
        switch (id) {
            case SAPLING:
            case DEAD_BUSH:
            case DANDELION:
            case ROSE:
            case RED_MUSHROOM:
            case BROWN_MUSHROOM:
            case CACTUS:
                return true;
            default:
                return false;
        }
    }

    @Override
    public String getName() {
        return "Flower Pot";
    }

    @Override
    public int getId() {
        return FLOWER_POT_BLOCK;
    }

    private boolean isSupportValid(Block block) {
        return block.isSolid() || block instanceof BlockFence || block instanceof BlockWall || block instanceof BlockHopper;
    }

    @Override
    public int onUpdate(int type) {
        if (type == Level.BLOCK_UPDATE_NORMAL) {
            if (!isSupportValid(down())) {
                level.useBreakOn(this);
                return type;
            }
        }
        return 0;
    }

    @Override
    public boolean place(Item item, Block block, Block target, BlockFace face, double fx, double fy, double fz, Player player) {
        if (!isSupportValid(down())) return false;
        CompoundTag nbt = new CompoundTag()
                .putString("id", BlockEntity.FLOWER_POT)
                .putInt("x", (int) this.x)
                .putInt("y", (int) this.y)
                .putInt("z", (int) this.z)
                .putShort("item", 0)
                .putInt("data", 0);
        if (item.hasCustomBlockData()) {
            for (Tag aTag : item.getCustomBlockData().getAllTags()) {
                nbt.put(aTag.getName(), aTag);
            }
        }
        BlockEntity.createBlockEntity(BlockEntity.FLOWER_POT, this.level.getChunk(block.getChunkX(), block.getChunkZ()), nbt);

        this.getLevel().setBlock(block, this, true, true);
        return true;
    }

    @Override
    public boolean canBeActivated() {
        return true;
    }

    @Override
    public boolean onActivate(Item item) {
        return this.onActivate(item, null);
    }

    @Override
    public boolean onActivate(Item item, Player player) {
        BlockEntity blockEntity = getLevel().getBlockEntity(this);
        if (!(blockEntity instanceof BlockEntityFlowerPot)) return false;
        if (blockEntity.namedTag.getShort("item") != 0 || blockEntity.namedTag.getInt("mData") != 0) return false;
        int itemID;
        int itemMeta;
        if (!canPlaceIntoFlowerPot(item.getId())) {
            if (!canPlaceIntoFlowerPot(item.getBlock().getId())) {
                return true;
            }
            itemID = item.getBlock().getId();
            itemMeta = item.getDamage();
        } else {
            itemID = item.getId();
            itemMeta = item.getDamage();
        }
        blockEntity.namedTag.putShort("item", itemID);
        blockEntity.namedTag.putInt("data", itemMeta);

        this.setDamage(1);
        this.getLevel().setBlock(this, this, true);
        ((BlockEntityFlowerPot) blockEntity).spawnToAll();

        if (!player.isCreative()) {
            item.setCount(item.getCount() - 1);
            player.getInventory().setItemInHand(item.getCount() > 0 ? item : Item.get(Item.AIR));
        }
        return true;
    }

    @Override
    public Item[] getDrops(Item item) {
        boolean dropInside = false;
        int insideID = 0;
        int insideMeta = 0;
        BlockEntity blockEntity = getLevel().getBlockEntity(this);
        if (blockEntity instanceof BlockEntityFlowerPot) {
            dropInside = true;
            insideID = blockEntity.namedTag.getShort("item");
            insideMeta = blockEntity.namedTag.getInt("data");
        }

        if (dropInside) {
            return new Item[]{
                    new ItemFlowerPot(),
                    Item.get(insideID, insideMeta, 1)
            };
        } else {
            return new Item[]{
                    new ItemFlowerPot()
            };
        }
    }

    @Override
    protected AxisAlignedBB recalculateBoundingBox() {
        return new AxisAlignedBB(this.x + 0.3125, this.y, this.z + 0.3125, this.x + 0.6875, this.y + 0.375, this.z + 0.6875);
    }

    @Override
    public boolean canPassThrough() {
        return false;
    }

    @Override
    public Item toItem() {
        return new ItemFlowerPot();
    }
}
