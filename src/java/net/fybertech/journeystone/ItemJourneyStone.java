package net.fybertech.journeystone;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Sounds;
import net.minecraft.item.EnumAction;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ItemUseResult;
import net.minecraft.util.MainOrOffHand;
import net.minecraft.util.ObjectActionHolder;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;


public class ItemJourneyStone extends Item
{
	public ItemJourneyStone() {
		this.setCreativeTab(CreativeTabs.tabTools);
		this.setUnlocalizedName("journeyStone");
		this.setMaxStackSize(1);
		this.setMaxDamage(4);
	}
	
	
	// Use this until we can support language files
	@Override
	public String getItemStackDisplayName(ItemStack param0) {
		return "Journey Stone";
	}
	
	
	@Override
	public int getMaxItemUseDuration(ItemStack param0) 
	{
		return 100;
	}

	
	@Override
	public EnumAction getItemUseAction(ItemStack param0) 
	{		
		return EnumAction.BOW;
	}	
		
	
	@Override
	public ItemStack onItemUseFinish(ItemStack stack, World world, EntityLivingBase player) 
	{
		// TODO - Stop item being used after teleport	
		// TODO - Leave behind particles
		
		boolean hasKeys = false;		
		NBTTagCompound tag = stack.getTagCompound();
		if (tag != null) {			
			if (tag.hasKey("teleportX", 3) && tag.hasKey("teleportY", 3) && tag.hasKey("teleportZ", 3)) hasKeys = true;
		}		
		
		if (hasKeys) {
			Vec3 pos = player.getPositionVector();
			
			int x = tag.getInteger("teleportX");
			int y = tag.getInteger("teleportY");
			int z = tag.getInteger("teleportZ");
			
			if (!world.isRemote) {
				//world.playSoundEffect(pos.xCoord, pos.yCoord, pos.zCoord, "mob.endermen.portal", 1, 1);
				world.playSoundEffect(null, pos.xCoord, pos.yCoord, pos.zCoord, Sounds.entity_endermen_teleport, 1, 1);
				
				player.fallDistance = 0.0F;
				player.setPositionAndUpdate(x + 0.5, y + 0.5, z + 0.5);
				
				//world.playSoundEffect(x + 0.5, y + 0.5, z + 0.5, "mob.endermen.portal", 1, 1);
				world.playSoundEffect(null, x + 0.5, y + 0.5, z + 0.5, Sounds.entity_endermen_teleport, 1, 1);
			}			
			
			stack.damageItem(1, player);			
		}	
		
		return super.onItemUseFinish(stack, world, player);
	}
	
	
	@Override
	public ItemUseResult onItemUse(ItemStack stack, EntityPlayer player, World world, BlockPos pos, MainOrOffHand hand, EnumFacing facing, float x, float y, float z) 
	{
		// TODO - Don't let you teleport in the wrong dimension
		
		boolean hasKeys = false;
		boolean hasTempKeys = false;
		
		NBTTagCompound tag = stack.getTagCompound();
		if (tag != null) {			
			if (tag.hasKey("teleportX", 3) && tag.hasKey("teleportY", 3) && tag.hasKey("teleportZ", 3)) hasKeys = true;
			if (tag.hasKey("confirmX", 3) && tag.hasKey("confirmY", 3) && tag.hasKey("confirmZ", 3)) hasTempKeys = true;
		}

		// Do it the hard way since we don't have enough mappings
		BlockPos destPos = null;		
		switch (facing.ordinal()) {
			case 0: destPos = pos.down(); break;
			case 1: destPos = pos.up(); break;
			case 2: destPos = pos.north(); break;
			case 3: destPos = pos.south(); break;
			case 4: destPos = pos.west(); break;
			case 5: destPos = pos.east(); break;
		}
		
		if (tag == null) { 
			tag = new NBTTagCompound();
			stack.setTagCompound(tag);
		}				
		
		if (!hasKeys && !hasTempKeys) {			
			tag.setInteger("confirmX", destPos.getX());
			tag.setInteger("confirmY", destPos.getY());
			tag.setInteger("confirmZ", destPos.getZ());				
	
			if (world.isRemote) player.addChatMessage(new ChatComponentText("Select again to confirm coordinates."));
		}
		else if (!hasKeys && hasTempKeys)
		{
			int cx = tag.getInteger("confirmX");
			int cy = tag.getInteger("confirmY");
			int cz = tag.getInteger("confirmZ");
			if (cx == destPos.getX() && cy == destPos.getY() && cz == destPos.getZ()) {
				tag.removeTag("confirmX");
				tag.removeTag("confirmY");
				tag.removeTag("confirmZ");
				tag.setInteger("teleportX", destPos.getX());
				tag.setInteger("teleportY", destPos.getY());
				tag.setInteger("teleportZ", destPos.getZ());
				if (world.isRemote) player.addChatMessage(new ChatComponentText("Coordinates saved."));
			}
			else {
				tag.setInteger("confirmX", destPos.getX());
				tag.setInteger("confirmY", destPos.getY());
				tag.setInteger("confirmZ", destPos.getZ());				
		
				if (world.isRemote) player.addChatMessage(new ChatComponentText("Select again to confirm coordinates."));
			}
		}
		
		return super.onItemUse(stack, player, world, pos, hand, facing, x, y, z);
	}
	
	
	@Override
	public ObjectActionHolder<ItemStack> onItemRightClick(ItemStack stack, World world, EntityPlayer player, MainOrOffHand hand) 
	{	
		boolean hasKeys = false;
		
		NBTTagCompound tag = stack.getTagCompound();
		if (tag != null) {			
			if (tag.hasKey("teleportX", 3) && tag.hasKey("teleportY", 3) && tag.hasKey("teleportZ", 3)) hasKeys = true;
		}
		
		if (hasKeys) {
			player.setItemInUse(hand);	
			return new ObjectActionHolder<ItemStack>(ItemUseResult.SUCCESS, stack);
		}
		else return new ObjectActionHolder<ItemStack>(ItemUseResult.FAIL, stack);
	}
}
