package dual.samurai;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;
import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.craftbukkit.v1_12_R1.entity.CraftEntity;
import org.bukkit.craftbukkit.v1_12_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityCombustByEntityEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerAnimationEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerVelocityEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.Vector;

import net.minecraft.server.v1_12_R1.DamageSource;
import net.minecraft.server.v1_12_R1.EnchantmentManager;
import net.minecraft.server.v1_12_R1.Entity;
import net.minecraft.server.v1_12_R1.EntityComplexPart;
import net.minecraft.server.v1_12_R1.EntityHuman;
import net.minecraft.server.v1_12_R1.EntityLiving;
import net.minecraft.server.v1_12_R1.EntityPlayer;
import net.minecraft.server.v1_12_R1.EnumHand;
import net.minecraft.server.v1_12_R1.EnumMonsterType;
import net.minecraft.server.v1_12_R1.EnumParticle;
import net.minecraft.server.v1_12_R1.GenericAttributes;
import net.minecraft.server.v1_12_R1.IComplex;
import net.minecraft.server.v1_12_R1.ItemStack;
import net.minecraft.server.v1_12_R1.ItemSword;
import net.minecraft.server.v1_12_R1.MathHelper;
import net.minecraft.server.v1_12_R1.MobEffects;
import net.minecraft.server.v1_12_R1.NBTTagCompound;
import net.minecraft.server.v1_12_R1.PacketPlayOutAnimation;
import net.minecraft.server.v1_12_R1.PacketPlayOutEntityVelocity;
import net.minecraft.server.v1_12_R1.SoundEffects;
import net.minecraft.server.v1_12_R1.StatisticList;
import net.minecraft.server.v1_12_R1.WorldServer;

public class DualSamurai extends JavaPlugin implements Listener{

	private static DualSamurai plugin;
	private static CustomConfig config;
	private static CustomConfig data;

	private Logger logger;
	private Language language;

	private HashMap<String, TabExecutor> commands = new HashMap<String, TabExecutor>();

	private List<String> list = new ArrayList<String>();
	private List<String> players = new ArrayList<String>();

	private HashMap<String, Double> d = new HashMap<String, Double>();

	@Override
	public void onEnable(){
		plugin = this;

		config = new CustomConfig(plugin);
		config.saveDefaultConfig();

		data = new CustomConfig(plugin, "data.yml");
		data.saveDefaultConfig();

		logger = getLogger();

		loadValues();

		commands.put("duals", new MainCommand(plugin));

		if(getServer().getClass().getPackage().getName().replaceFirst(".*(\\d+_\\d+_R\\d+).*", "$1").equals("1_12_R1")){
			getServer().getPluginManager().registerEvents(plugin, plugin);
		}else{
			if(Language.isJp(language))info("このバージョンは対応されていません。");
			else info("not supported version.");
		}

		loadPlayers();

		d.put("WOOD_SWORD", 4d);
		d.put("STONE_SWORD", 5d);
		d.put("IRON_SWORD", 6d);
		d.put("GOLD_SWORD", 4d);
		d.put("DIAMOND_SWORD", 7d);
		d.put("WOOD_AXE", 7d);
		d.put("STONE_AXE", 9d);
		d.put("IRON_AXE", 9d);
		d.put("GOLD_AXE", 7d);
		d.put("DIAMOND_AXE", 9d);
		d.put("WOOD_PICKAXE", 2d);
		d.put("STONE_PICKAXE", 3d);
		d.put("IRON_PICKAXE", 4d);
		d.put("GOLD_PICKAXE", 2d);
		d.put("DIAMOND_PICKAXE", 5d);
		d.put("WOOD_SPADE", 2.5);
		d.put("STONE_SPADE", 3.5);
		d.put("IRON_SPADE", 4.5);
		d.put("GOLD_SPADE", 2.5);
		d.put("DIAMOND_SPADE", 5.5);
		d.put("WOOD_HOE", 1d);
		d.put("STONE_HOE", 1d);
		d.put("IRON_}HOE", 1d);
		d.put("GOLD_HOE", 1d);
		d.put("DIAMOND_HOE", 1d);

		info("DualWielding is enable!");
	}

	@Override
	public void onDisable(){
		info("DualWielding is disable!");
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args){
		return commands.get(command.getName()).onCommand(sender, command, label, args);
	}

	@EventHandler
	public void test(EntityDamageByEntityEvent e){
		System.out.println(e.getDamage());
	}

	public static DualSamurai getDualSamurai(){
		return plugin;
	}

	public static CustomConfig getCustomConfig(){
		return config;
	}

	public static CustomConfig getDataConfig(){
		return data;
	}

	public void info(String s){
		logger.info(s);
	}

	public void loadValues(){
		FileConfiguration f = config.getConfig();
		list = f.getStringList("Materials");
		if(f.getString("Language").equals("Japanese"))language = Language.Japanese;
		else language = Language.English;
	}

	public void loadPlayers(){
		getServer().getOnlinePlayers().forEach(p -> {
			if(isEnable(p.getUniqueId()))players.add(p.getName());
		});
	}

	public Language getLanguage(){
		return language;
	}

	public void setLanguage(Language language){
		config.getConfig().set("Language", language.name());
		config.updateConfig();
		this.language = language;
	}

	public boolean isEnable(UUID uuid){
		return data.getConfig().getBoolean(uuid.toString() + ".Enable");
	}

	public void setEnable(UUID uuid, boolean b){
		data.getConfig().set(uuid.toString() + ".Enable", b);
		data.updateConfig();
	}

	public void addPlayer(String s){
		players.add(s);
	}

	public void removePlayer(String s){
		players.remove(s);
	}

	public boolean enablePlayer(String s){
		return players.contains(s);
	}

	@EventHandler
	public void onPlayerJoinEvent(PlayerJoinEvent e){
		Player p = e.getPlayer();
		if(isEnable(p.getUniqueId()))players.add(p.getName());
	}

	@EventHandler
	public void onPlayerQuitEvent(PlayerQuitEvent e){
		Player p = e.getPlayer();
		if(isEnable(p.getUniqueId()))players.remove(p.getName());
	}

	@EventHandler
	public void onPlayerInteractEvent(PlayerInteractEvent e){
		Player p = e.getPlayer();
		if(e.getHand().equals(EquipmentSlot.OFF_HAND) && enablePlayer(p.getName())){
			PlayerAnimationEvent ev = new PlayerAnimationEvent(p);
			if(!ev.isCancelled()){
				EntityPlayer ep = ((CraftPlayer) p).getHandle();
				PacketPlayOutAnimation packet = new PacketPlayOutAnimation(ep, 3);
				ep.playerConnection.sendPacket(packet);
			}
		}
	}

	@EventHandler
	public void onPlayerInteractEntityEvent(PlayerInteractEntityEvent e){
		if(e.isCancelled())return;
		Player p = e.getPlayer();
		if(e.getHand().equals(EquipmentSlot.OFF_HAND) && enablePlayer(p.getName())){
			org.bukkit.inventory.ItemStack off = p.getInventory().getItemInOffHand();
			if(off != null){
				Material m = off.getType();
				String s = m.toString();
				if(m != null && !m.equals(Material.AIR) && list.contains(s)){
					attack(p, ((CraftEntity) e.getRightClicked()).getHandle());
				}
			}
		}
	}

	public void o(Object o){
		System.out.println(o);
	}

	@SuppressWarnings("deprecation")
	public void attack(Player p, Entity entity) {
		EntityPlayer ep = ((CraftPlayer) p).getHandle();
		if (entity.bd() && !entity.t(ep)){
			double mainDamage = 0;
			org.bukkit.inventory.ItemStack main = p.getInventory().getItemInMainHand();
			if(main != null){
				Material m = main.getType();
				if(m != null && !m.equals(Material.AIR)){
					if(ep.getItemInMainHand().hasTag()){
						NBTTagCompound tag0 = ep.getItemInMainHand().getTag();
						mainDamage = tag0.getDouble("generic.attackDamage");
					}else{
						if(d.containsKey(m.toString()))mainDamage = d.get(m.toString());
						else mainDamage = 1;
					}
				}else{
					mainDamage = 1;
				}
			}
			ItemStack off = ep.getItemInOffHand();
			double offDamage = 0;
			if(off.hasTag()){
				NBTTagCompound tag1 = off.getTag();
				offDamage = tag1.getDouble("generic.attackDamage");
			}else{
				offDamage = d.get(p.getInventory().getItemInOffHand().getType().toString());
			}
			float f = (float) (ep.getAttributeInstance(GenericAttributes.ATTACK_DAMAGE).getValue() - mainDamage + offDamage);
			float f1;
			if (entity instanceof EntityLiving) {
				f1 = EnchantmentManager.a(off, ((EntityLiving) entity).getMonsterType());
			} else {
				f1 = EnchantmentManager.a(off, EnumMonsterType.UNDEFINED);
			}

			float f2 = ep.n(0.5F);
			f *= 0.2F + f2 * f2 * 0.8F;
			f1 *= f2;
			ep.ds();
			if (f > 0.0F || f1 > 0.0F) {
				boolean flag = f2 > 0.9F;
				boolean flag1 = false;
				byte b0 = 0;
				int i = b0 + EnchantmentManager.b(ep);
				if (ep.isSprinting() && flag) {
					ep.world.a((EntityHuman) null, ep.locX, ep.locY, ep.locZ, SoundEffects.fw, ep.bK(), 1.0F,
							1.0F);
					++i;
					flag1 = true;
				}

				boolean flag2 = flag && ep.fallDistance > 0.0F && !ep.onGround && !ep.m_() && !ep.isInWater()
						&& !ep.hasEffect(MobEffects.BLINDNESS) && !ep.isPassenger()
						&& entity instanceof EntityLiving;
				flag2 = flag2 && !ep.isSprinting();
				if (flag2) {
					f *= 1.5F;
				}

				f += f1;
				boolean flag3 = false;
				double d0 = (double) (ep.J - ep.I);
				if (flag && !flag2 && !flag1 && ep.onGround && d0 < (double) ep.cy()) {
					ItemStack f3 = ep.b((EnumHand) EnumHand.OFF_HAND);
					if (f3.getItem() instanceof ItemSword) {
						flag3 = true;
					}
				}

				float arg26 = 0.0F;
				boolean flag4 = false;
				int j = EnchantmentManager.getFireAspectEnchantmentLevel(ep);
				if (entity instanceof EntityLiving) {
					arg26 = ((EntityLiving) entity).getHealth();
					if (j > 0 && !entity.isBurning()) {
						EntityCombustByEntityEvent d1 = new EntityCombustByEntityEvent(ep.getBukkitEntity(),
								entity.getBukkitEntity(), 1);
						Bukkit.getPluginManager().callEvent(d1);
						if (!d1.isCancelled()) {
							flag4 = true;
							entity.setOnFire(d1.getDuration());
						}
					}
				}

				double arg27 = entity.motX;
				double d2 = entity.motY;
				double d3 = entity.motZ;
				boolean flag5 = entity.damageEntity(DamageSource.playerAttack(ep), f);
				//processing finish
				if (flag5) {
					if (i > 0) {
						if (entity instanceof EntityLiving) {
							((EntityLiving) entity).a(ep, (float) i * 0.5F,
									(double) MathHelper.sin(ep.yaw * 0.017453292F),
									(double) (-MathHelper.cos(ep.yaw * 0.017453292F)));
						} else {
							entity.f((double) (-MathHelper.sin(ep.yaw * 0.017453292F) * (float) i * 0.5F), 0.1D,
									(double) (MathHelper.cos(ep.yaw * 0.017453292F) * (float) i * 0.5F));
						}

						ep.motX *= 0.6D;
						ep.motZ *= 0.6D;
						ep.setSprinting(false);
					}

					if (flag3) {
						float itemstack1 = 1.0F + EnchantmentManager.a(ep) * f;
						List<EntityLiving> object = ep.world.a(EntityLiving.class, entity.getBoundingBox().grow(1.0D, 0.25D, 1.0D));
						Iterator<EntityLiving> f5 = object.iterator();

						while (f5.hasNext()) {
							EntityLiving k = (EntityLiving) f5.next();
							if (k != ep && k != entity && !ep.r(k) && ep.h(k) < 9.0D
									&& k.damageEntity(DamageSource.playerAttack(ep).sweep(), itemstack1)) {
								k.a(ep, 0.4F, (double) MathHelper.sin(ep.yaw * 0.017453292F),
										(double) (-MathHelper.cos(ep.yaw * 0.017453292F)));
							}
						}

						ep.world.a((EntityHuman) null, ep.locX, ep.locY, ep.locZ, SoundEffects.fz, ep.bK(),
								1.0F, 1.0F);
						ep.cX();
					}

					if (entity instanceof EntityPlayer && entity.velocityChanged) {
						boolean arg28 = false;
						Player arg29 = (Player) entity.getBukkitEntity();
						Vector arg32 = new Vector(arg27, d2, d3);
						PlayerVelocityEvent arg35 = new PlayerVelocityEvent(arg29, arg32.clone());
						ep.world.getServer().getPluginManager().callEvent(arg35);
						if (arg35.isCancelled()) {
							arg28 = true;
						} else if (!arg32.equals(arg35.getVelocity())) {
							arg29.setVelocity(arg35.getVelocity());
						}

						if (!arg28) {
							((EntityPlayer) entity).playerConnection
									.sendPacket(new PacketPlayOutEntityVelocity(entity));
							entity.velocityChanged = false;
							entity.motX = arg27;
							entity.motY = d2;
							entity.motZ = d3;
						}
					}

					if (flag2) {
						ep.world.a((EntityHuman) null, ep.locX, ep.locY, ep.locZ, SoundEffects.fv, ep.bK(),
								1.0F, 1.0F);
						ep.a(entity);
					}

					if (!flag2 && !flag3) {
						if (flag) {
							ep.world.a((EntityHuman) null, ep.locX, ep.locY, ep.locZ, SoundEffects.fy,
									ep.bK(), 1.0F, 1.0F);
						} else {
							ep.world.a((EntityHuman) null, ep.locX, ep.locY, ep.locZ, SoundEffects.fA,
									ep.bK(), 1.0F, 1.0F);
						}
					}

					if (f1 > 0.0F) {
						ep.b(entity);
					}

					ep.z(entity);
					if (entity instanceof EntityLiving) {
						EnchantmentManager.a((EntityLiving) entity, ep);
					}

					EnchantmentManager.b(ep, entity);
					ItemStack arg30 = ep.getItemInOffHand();
					Object arg31 = entity;
					if (entity instanceof EntityComplexPart) {
						IComplex arg33 = ((EntityComplexPart) entity).owner;
						if (arg33 instanceof EntityLiving) {
							arg31 = (EntityLiving) arg33;
						}
					}

					if (!arg30.isEmpty() && arg31 instanceof EntityLiving) {
						arg30.a((EntityLiving) arg31, ep);
						if (arg30.isEmpty()) {
							ep.a((EnumHand) EnumHand.OFF_HAND, (ItemStack) ItemStack.a);
						}
					}

					if (entity instanceof EntityLiving) {
						float arg34 = arg26 - ((EntityLiving) entity).getHealth();
						ep.a(StatisticList.y, Math.round(arg34 * 10.0F));
						if (j > 0) {
							EntityCombustByEntityEvent arg36 = new EntityCombustByEntityEvent(ep.getBukkitEntity(),
									entity.getBukkitEntity(), j * 4);
							Bukkit.getPluginManager().callEvent(arg36);
							if (!arg36.isCancelled()) {
								entity.setOnFire(arg36.getDuration());
							}
						}

						if (ep.world instanceof WorldServer && arg34 > 2.0F) {
							int arg37 = (int) ((double) arg34 * 0.5D);
							((WorldServer) ep.world).a(EnumParticle.DAMAGE_INDICATOR, entity.locX,
									entity.locY + (double) (entity.length * 0.5F), entity.locZ, arg37, 0.1D, 0.0D, 0.1D,
									0.2D, new int[0]);
						}
					}

					ep.applyExhaustion(ep.world.spigotConfig.combatExhaustion);
				} else {
					ep.world.a((EntityHuman) null, ep.locX, ep.locY, ep.locZ, SoundEffects.fx, ep.bK(), 1.0F,
							1.0F);
					if (flag4) {
						entity.extinguish();
					}

					if (ep instanceof EntityPlayer) {
						ep.getBukkitEntity().updateInventory();
					}
				}
			}
		}
	}

}
