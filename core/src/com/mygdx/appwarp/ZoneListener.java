package com.mygdx.appwarp;

import com.shephertz.app42.gaming.multiplayer.client.WarpClient;
import com.shephertz.app42.gaming.multiplayer.client.command.WarpResponseResultCode;
import com.shephertz.app42.gaming.multiplayer.client.events.AllRoomsEvent;
import com.shephertz.app42.gaming.multiplayer.client.events.AllUsersEvent;
import com.shephertz.app42.gaming.multiplayer.client.events.LiveRoomInfoEvent;
import com.shephertz.app42.gaming.multiplayer.client.events.LiveUserInfoEvent;
import com.shephertz.app42.gaming.multiplayer.client.events.MatchedRoomsEvent;
import com.shephertz.app42.gaming.multiplayer.client.events.RoomData;
import com.shephertz.app42.gaming.multiplayer.client.events.RoomEvent;
import com.shephertz.app42.gaming.multiplayer.client.listener.RoomRequestListener;
import com.shephertz.app42.gaming.multiplayer.client.listener.ZoneRequestListener;

/**Listener that listens to lobby notifications such as onGetMatchedRoomsDone and onCreateRoomDone*/
public class ZoneListener implements ZoneRequestListener{

	
	private WarpController callBack;
	
	public ZoneListener(WarpController callBack) {
		this.callBack = callBack;
	}

	@Override
	public void onCreateRoomDone (RoomEvent re) {
		System.out.println("onCreateRoomDone: " + re.getResult());
		if(re.getResult()==WarpResponseResultCode.SUCCESS){
			System.out.println("Room is created successfully.");
			callBack.onRoomCreated(re.getData().getId());
		}else{
			callBack.onRoomCreated(null);
		}
	}

	@Override
	public void onDeleteRoomDone (RoomEvent arg0) {
		System.out.println("onDeleteRoomDone: "+arg0.getResult());
	}

	@Override
	public void onGetAllRoomsDone (AllRoomsEvent arg0) {
		
		
	}

	@Override
	public void onGetLiveUserInfoDone (LiveUserInfoEvent arg0) {
		callBack.onGetUserInfo(arg0);
	}

	@Override
	public void onGetMatchedRoomsDone (MatchedRoomsEvent me) {
		RoomData[] roomDataList = me.getRoomsData();
		if (WarpController.isDeleteFlag()){
			if(roomDataList!=null && roomDataList.length>0){
				try {
					WarpClient warpClient = WarpClient.getInstance();
					for (RoomData roomData:roomDataList){
						warpClient.deleteRoom(roomData.getId());
					}
				} catch (Exception ex) {
					System.out.println("Fail to get warpClient");
				}
			}
			WarpController.setDeleteFlag(false);
			WarpController.setWaitRoomFlag(true);
		} else {
			if(roomDataList!=null && roomDataList.length>0){
				WarpController.setRoomDatas(roomDataList);
			}else{
				WarpController.setRoomDatas(null);
			}
		}
	}

	@Override
	public void onGetOnlineUsersDone (AllUsersEvent arg0) {
		
		
	}

	@Override
	public void onSetCustomUserDataDone (LiveUserInfoEvent arg0) {
	}
	

}
