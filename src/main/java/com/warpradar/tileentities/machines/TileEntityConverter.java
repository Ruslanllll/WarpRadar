package com.warpradar.tileentities.machines;

import java.util.HashMap;

import com.hbm.inventory.fluid.FluidType;
import com.hbm.inventory.fluid.Fluids;
import com.hbm.inventory.fluid.tank.FluidTank;
import com.hbm.items.machine.IItemFluidIdentifier;
import com.hbm.tileentity.TileEntityLoadedBase;
import com.hbm.tileentity.TileEntityMachineBase;

import api.hbm.fluid.IFluidStandardReceiver;
import api.hbm.fluid.IFluidStandardTransceiver;
import net.minecraft.block.BlockContainer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTankInfo;
import net.minecraftforge.fluids.IFluidHandler;

public class TileEntityConverter extends TileEntityMachineBase implements IFluidStandardReceiver, IFluidHandler {
  public static HashMap<FluidType, Fluid> FluidMap = new HashMap<>();

  public FluidTank bobtank;
  public net.minecraftforge.fluids.FluidTank forgetank;

  public TileEntityConverter() {
    super(0);
    bobtank = new FluidTank(Fluids.NONE, 12000);
    forgetank = new net.minecraftforge.fluids.FluidTank(12000);
  }

  @Override
  public String getName() {
    return "tile.fluidconverter";
  }

  @Override
  public void updateEntity() {

    if (!worldObj.isRemote && bobtank.getTankType() != Fluids.NONE) {
      if (hasEnoughtBobFluid() && FluidMap.containsKey(bobtank.getTankType())
          && forgetank.getFluid().amount < forgetank.getCapacity()
          && (forgetank.getFluid().getFluid() == FluidMap.get(bobtank.getTankType())
              || forgetank.getFluid().getFluid() == null)) {
        int amount = Math.min(forgetank.getCapacity() - forgetank.getFluidAmount(), bobtank.getFill());
        bobtank.setFill(bobtank.getFill() - amount);
        FluidStack fluidStack = new FluidStack(FluidMap.get(bobtank.getTankType()), amount);
        forgetank.fill(fluidStack, true);
      }
      this.subscribeToAllAround(bobtank.getTankType(), this);
    }
  }

  private boolean hasEnoughtBobFluid() {
    return bobtank.getFill() >= 1000;
  }

  @Override
  public FluidTank[] getReceivingTanks() {
    FluidTank a[] = { bobtank };
    return a;
  }

  @Override
  public FluidTank[] getAllTanks() {
    FluidTank a[] = { bobtank };
    return a;
  }

  @Override
  public int fill(ForgeDirection from, FluidStack resource, boolean doFill) {
    return forgetank.fill(resource, doFill);
  }

  @Override
  public FluidStack drain(ForgeDirection from, FluidStack resource, boolean doDrain) {
    if (resource == null || !resource.isFluidEqual(forgetank.getFluid())) {
      return null;
    }
    return forgetank.drain(resource.amount, doDrain);
  }

  @Override
  public FluidStack drain(ForgeDirection from, int maxDrain, boolean doDrain) {
    return forgetank.drain(maxDrain, doDrain);
  }

  @Override
  public FluidTankInfo[] getTankInfo(ForgeDirection from) {
    return new FluidTankInfo[] { forgetank.getInfo() };
  }

  @Override
  public boolean canFill(ForgeDirection from, Fluid fluid) {
    return false;
  }

  @Override
  public boolean canDrain(ForgeDirection from, Fluid fluid) {
    return true;
  }

  @Override
  public void readFromNBT(NBTTagCompound nbt) {
    super.readFromNBT(nbt);
    this.bobtank.readFromNBT(nbt, bobtank.getTankType().getUnlocalizedName());
    this.forgetank.readFromNBT(nbt);
  }

  @Override
  public void writeToNBT(NBTTagCompound nbt) {
    super.writeToNBT(nbt);
    this.bobtank.writeToNBT(nbt, bobtank.getTankType().getUnlocalizedName());
    this.forgetank.writeToNBT(nbt);
  }

}
