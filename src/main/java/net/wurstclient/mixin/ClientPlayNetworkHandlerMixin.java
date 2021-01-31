/*
 * Copyright (c) 2014-2021 Wurst-Imperium and contributors.
 *
 * This source code is subject to the terms of the GNU General Public
 * License, version 3. If a copy of the GPL was not distributed with this
 * file, You can obtain one at: https://www.gnu.org/licenses/gpl-3.0.txt
 */
package net.wurstclient.mixin;

import net.minecraft.client.network.play.ClientPlayNetHandler;
import net.minecraft.client.network.play.IClientPlayNetHandler;
import net.minecraft.network.IPacket;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.*;
import net.minecraft.util.text.ITextComponent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.wurstclient.event.EventManager;
import net.wurstclient.events.PacketOutputListener.PacketOutputEvent;

@Mixin(ClientPlayNetHandler.class)
public class ClientPlayNetworkHandlerMixin implements IClientPlayNetHandler
{
	@Inject(at = {@At("HEAD")},
		method = {"sendPacket(Lnet/minecraft/network/IPacket;)V"},
		cancellable = true)
	private void onSendPacket(IPacket<?> packet, CallbackInfo ci)
	{
		PacketOutputEvent event = new PacketOutputEvent(packet);
		EventManager.fire(event);
		
		if(event.isCancelled())
			ci.cancel();
	}
	
	@Shadow
	@Override
	public void onDisconnect(ITextComponent var1)
	{
		
	}
	
	@Shadow
	@Override
	public NetworkManager getNetworkManager()
	{
		return null;
	}
	
	@Shadow
	@Override
	public void handleSpawnObject(SSpawnObjectPacket var1)
	{
		
	}
	
	@Shadow
	@Override
	public void handleSpawnExperienceOrb(SSpawnExperienceOrbPacket var1)
	{
		
	}
	
	@Shadow
	@Override
	public void handleSpawnMob(SSpawnMobPacket var1)
	{
		
	}
	
	@Shadow
	@Override
	public void handleScoreboardObjective(
			SScoreboardObjectivePacket var1)
	{
		
	}
	
	@Shadow
	@Override
	public void handleSpawnPainting(SSpawnPaintingPacket var1)
	{
		
	}

	@Shadow
	@Override
	public void handleSpawnPlayer(SSpawnPlayerPacket packetIn) {

	}

	@Shadow
	@Override
	public void handleAnimation(SAnimateHandPacket packetIn) {

	}

	@Shadow
	@Override
	public void handleStatistics(SStatisticsPacket packetIn) {

	}

	@Shadow
	@Override
	public void handleRecipeBook(SRecipeBookPacket packetIn) {

	}

	@Shadow
	@Override
	public void handleBlockBreakAnim(SAnimateBlockBreakPacket packetIn) {

	}

	@Shadow
	@Override
	public void handleSignEditorOpen(SOpenSignMenuPacket packetIn) {

	}

	@Shadow
	@Override
	public void handleUpdateTileEntity(SUpdateTileEntityPacket packetIn) {

	}

	@Shadow
	@Override
	public void handleBlockAction(SBlockActionPacket packetIn) {

	}

	@Shadow
	@Override
	public void handleBlockChange(SChangeBlockPacket packetIn) {

	}

	@Shadow
	@Override
	public void handleChat(SChatPacket packetIn) {

	}

	@Shadow
	@Override
	public void handleMultiBlockChange(SMultiBlockChangePacket packetIn) {

	}

	@Shadow
	@Override
	public void handleMaps(SMapDataPacket packetIn) {

	}

	@Shadow
	@Override
	public void handleConfirmTransaction(SConfirmTransactionPacket packetIn) {

	}

	@Shadow
	@Override
	public void handleCloseWindow(SCloseWindowPacket packetIn) {

	}

	@Shadow
	@Override
	public void handleWindowItems(SWindowItemsPacket packetIn) {

	}

	@Shadow
	@Override
	public void handleOpenHorseWindow(SOpenHorseWindowPacket packetIn) {

	}

	@Shadow
	@Override
	public void handleWindowProperty(SWindowPropertyPacket packetIn) {

	}

	@Shadow
	@Override
	public void handleSetSlot(SSetSlotPacket packetIn) {

	}

	@Shadow
	@Override
	public void handleCustomPayload(SCustomPayloadPlayPacket packetIn) {

	}

	@Shadow
	@Override
	public void handleDisconnect(SDisconnectPacket packetIn) {

	}

	@Shadow
	@Override
	public void handleEntityStatus(SEntityStatusPacket packetIn) {

	}

	@Shadow
	@Override
	public void handleEntityAttach(SMountEntityPacket packetIn) {

	}

	@Shadow
	@Override
	public void handleSetPassengers(SSetPassengersPacket packetIn) {

	}

	@Shadow
	@Override
	public void handleExplosion(SExplosionPacket packetIn) {

	}

	@Shadow
	@Override
	public void handleChangeGameState(SChangeGameStatePacket packetIn) {

	}

	@Shadow
	@Override
	public void handleKeepAlive(SKeepAlivePacket packetIn) {

	}

	@Shadow
	@Override
	public void handleChunkData(SChunkDataPacket packetIn) {

	}

	@Shadow
	@Override
	public void processChunkUnload(SUnloadChunkPacket packetIn) {

	}

	@Shadow
	@Override
	public void handleEffect(SPlaySoundEventPacket packetIn) {

	}

	@Shadow
	@Override
	public void handleJoinGame(SJoinGamePacket packetIn) {

	}

	@Shadow
	@Override
	public void handleEntityMovement(SEntityPacket packetIn) {

	}

	@Shadow
	@Override
	public void handlePlayerPosLook(SPlayerPositionLookPacket packetIn) {

	}

	@Shadow
	@Override
	public void handleParticles(SSpawnParticlePacket packetIn) {

	}

	@Shadow
	@Override
	public void handlePlayerAbilities(SPlayerAbilitiesPacket packetIn) {

	}

	@Shadow
	@Override
	public void handlePlayerListItem(SPlayerListItemPacket packetIn) {

	}

	@Shadow
	@Override
	public void handleDestroyEntities(SDestroyEntitiesPacket packetIn) {

	}

	@Shadow
	@Override
	public void handleRemoveEntityEffect(SRemoveEntityEffectPacket packetIn) {

	}

	@Shadow
	@Override
	public void handleRespawn(SRespawnPacket packetIn) {

	}

	@Shadow
	@Override
	public void handleEntityHeadLook(SEntityHeadLookPacket packetIn) {

	}

	@Shadow
	@Override
	public void handleHeldItemChange(SHeldItemChangePacket packetIn) {

	}

	@Shadow
	@Override
	public void handleDisplayObjective(SDisplayObjectivePacket packetIn) {

	}

	@Shadow
	@Override
	public void handleEntityMetadata(SEntityMetadataPacket packetIn) {

	}

	@Shadow
	@Override
	public void handleEntityVelocity(SEntityVelocityPacket packetIn) {

	}

	@Shadow
	@Override
	public void handleEntityEquipment(SEntityEquipmentPacket packetIn) {

	}

	@Shadow
	@Override
	public void handleSetExperience(SSetExperiencePacket packetIn) {

	}

	@Shadow
	@Override
	public void handleUpdateHealth(SUpdateHealthPacket packetIn) {

	}

	@Shadow
	@Override
	public void handleTeams(STeamsPacket packetIn) {

	}

	@Shadow
	@Override
	public void handleUpdateScore(SUpdateScorePacket packetIn) {

	}

	@Shadow
	@Override
	public void func_230488_a_(SWorldSpawnChangedPacket p_230488_1_) {

	}

	@Shadow
	@Override
	public void handleTimeUpdate(SUpdateTimePacket packetIn) {

	}

	@Shadow
	@Override
	public void handleSoundEffect(SPlaySoundEffectPacket packetIn) {

	}

	@Shadow
	@Override
	public void handleSpawnMovingSoundEffect(SSpawnMovingSoundEffectPacket packetIn) {

	}

	@Shadow
	@Override
	public void handleCustomSound(SPlaySoundPacket packetIn) {

	}

	@Shadow
	@Override
	public void handleCollectItem(SCollectItemPacket packetIn) {

	}

	@Shadow
	@Override
	public void handleEntityTeleport(SEntityTeleportPacket packetIn) {

	}

	@Shadow
	@Override
	public void handleEntityProperties(SEntityPropertiesPacket packetIn) {

	}

	@Shadow
	@Override
	public void handleEntityEffect(SPlayEntityEffectPacket packetIn) {

	}

	@Shadow
	@Override
	public void handleTags(STagsListPacket packetIn) {

	}

	@Shadow
	@Override
	public void handleCombatEvent(SCombatPacket packetIn) {

	}

	@Shadow
	@Override
	public void handleServerDifficulty(SServerDifficultyPacket packetIn) {

	}

	@Shadow
	@Override
	public void handleCamera(SCameraPacket packetIn) {

	}

	@Shadow
	@Override
	public void handleWorldBorder(SWorldBorderPacket packetIn) {

	}

	@Shadow
	@Override
	public void handleTitle(STitlePacket packetIn) {

	}

	@Shadow
	@Override
	public void handlePlayerListHeaderFooter(SPlayerListHeaderFooterPacket packetIn) {

	}

	@Shadow
	@Override
	public void handleResourcePack(SSendResourcePackPacket packetIn) {

	}

	@Shadow
	@Override
	public void handleUpdateBossInfo(SUpdateBossInfoPacket packetIn) {

	}

	@Shadow
	@Override
	public void handleCooldown(SCooldownPacket packetIn) {

	}

	@Shadow
	@Override
	public void handleMoveVehicle(SMoveVehiclePacket packetIn) {

	}

	@Shadow
	@Override
	public void handleAdvancementInfo(SAdvancementInfoPacket packetIn) {

	}

	@Shadow
	@Override
	public void handleSelectAdvancementsTab(SSelectAdvancementsTabPacket packetIn) {

	}

	@Shadow
	@Override
	public void handlePlaceGhostRecipe(SPlaceGhostRecipePacket packetIn) {

	}

	@Shadow
	@Override
	public void handleCommandList(SCommandListPacket packetIn) {

	}

	@Shadow
	@Override
	public void handleStopSound(SStopSoundPacket packetIn) {

	}

	@Shadow
	@Override
	public void handleTabComplete(STabCompletePacket packetIn) {

	}

	@Shadow
	@Override
	public void handleUpdateRecipes(SUpdateRecipesPacket packetIn) {

	}

	@Shadow
	@Override
	public void handlePlayerLook(SPlayerLookPacket packetIn) {

	}

	@Shadow
	@Override
	public void handleNBTQueryResponse(SQueryNBTResponsePacket packetIn) {

	}

	@Shadow
	@Override
	public void handleUpdateLight(SUpdateLightPacket packetIn) {

	}

	@Shadow
	@Override
	public void handleOpenBookPacket(SOpenBookWindowPacket packetIn) {

	}

	@Shadow
	@Override
	public void handleOpenWindowPacket(SOpenWindowPacket packetIn) {

	}

	@Shadow
	@Override
	public void handleMerchantOffers(SMerchantOffersPacket packetIn) {

	}

	@Shadow
	@Override
	public void handleUpdateViewDistancePacket(SUpdateViewDistancePacket packetIn) {

	}

	@Shadow
	@Override
	public void handleChunkPositionPacket(SUpdateChunkPositionPacket packetIn) {

	}

	@Shadow
	@Override
	public void handleAcknowledgePlayerDigging(SPlayerDiggingPacket packetIn) {

	}

}
