package com.mygdx.appwarp;


import java.util.HashMap;
import java.util.Hashtable;

import org.json.JSONObject;

import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.utils.Json;
import com.shephertz.app42.gaming.multiplayer.client.Constants;
import com.shephertz.app42.gaming.multiplayer.client.WarpClient;
import com.shephertz.app42.gaming.multiplayer.client.command.WarpResponseResultCode;
import com.shephertz.app42.gaming.multiplayer.client.events.LiveRoomInfoEvent;
import com.shephertz.app42.gaming.multiplayer.client.events.LiveUserInfoEvent;
import com.shephertz.app42.gaming.multiplayer.client.events.RoomData;
import com.shephertz.app42.gaming.multiplayer.client.events.RoomEvent;

public class WarpController {

	private static WarpController instance;
	
	private boolean showLog = true;
	
	private final String apiKey = "ae293bed82680eb9c8103f1e383e7e76067e40dfe0071873b9d34886ec3eb878";
	private final String secretKey = "10fca0a440aa2412530f11c3343d224291d5fece843d2bb566819f7957fd0b19";
	
	private WarpClient warpClient;

	private static String localUser;
	private static String roomId;
	private static String[] liveUsers;

	private static HashMap<String,String> statusMap = new HashMap<String,String>();

	private boolean isConnected = false;
	boolean isUDPEnabled = false;

	private static RoomData[] roomDatas = null;
	private static RoomData room;

	private WarpListener warpListener ;
	private static Label labelChat;
	
	private int STATE;
	
	// Game state constants
	public static final int WAITING = 1;
	public static final int STARTED = 2;
	public static final int COMPLETED = 3;
	public static final int FINISHED = 4;
	
	// Game completed constants
	public static final int GAME_WIN = 5;
	public static final int GAME_LOOSE = 6;
	public static final int ENEMY_LEFT = 7;

	private static volatile boolean waitflag = false;
	private static volatile boolean waitRoomFlag = false;
	private static volatile boolean statusflag = false;
	
	public WarpController() {
		initAppwarp();
		warpClient.addConnectionRequestListener(new ConnectionListener(this));
		warpClient.addChatRequestListener(new ChatListener(this));
		warpClient.addZoneRequestListener(new ZoneListener(this));
		warpClient.addRoomRequestListener(new RoomListener(this));
		warpClient.addNotificationListener(new NotificationListener(this));
	}
	
	public static WarpController getInstance(){
		if(instance == null){
			instance = new WarpController();
		}
		return instance;
	}
	
	public void startApp(String localUser){
		this.localUser = localUser;
		warpClient.connectWithUserName(localUser);
	}
	
	public void setListener(WarpListener listener){
		this.warpListener = listener;
	}
	
	public void stopApp(){
		if(isConnected){
			warpClient.unsubscribeRoom(roomId);
			warpClient.leaveRoom(roomId);
		}
		warpClient.disconnect();
	}
	
	private void initAppwarp(){
		try {
			WarpClient.initialize(apiKey, secretKey);
			warpClient = WarpClient.getInstance();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void sendGameUpdate(String msg){
		if(isConnected){
			if(isUDPEnabled){
				warpClient.sendUDPUpdatePeers((localUser+"#@"+msg).getBytes());
			}else{
				warpClient.sendUpdatePeers((localUser+"#@"+msg).getBytes());
			}
		}
	}
	
	public void updateResult(int code, String msg){
		if(isConnected){
			STATE = COMPLETED;
			HashMap<String, Object> properties = new HashMap<String, Object>();
			properties.put("result", code);
			warpClient.lockProperties(properties);
		}
	}
	
	public void onConnectDone(boolean status){
		log("onConnectDone: "+status);
		statusflag = status;
		setWaitflag(true);
	}
	
	public void onDisconnectDone(boolean status){
		
	}
	
	public void onRoomCreated(String roomId){
		if(roomId!=null){
			// join room is nested in subscribe loop
			// we wan to always subscribe before we join room
			// although it may not be that necessary for creating room
			// but it is a good practice
			warpClient.subscribeRoom(roomId);
		}else{
			handleError();
		}
	}

	public void onJoinRoomDone(String roomId){
		log("onJoinRoomDone: "+roomId);
		if(roomId!=null){// success case
			isConnected = true;
			warpClient.getLiveRoomInfo(roomId);
		}else{
			warpClient.disconnect();
			handleError();
		}
	}

	public void onRoomSubscribed(RoomEvent event){
		log("onSubscribeRoomDone: "+event.getData().getId());
		if(event.getResult()==WarpResponseResultCode.SUCCESS) {// success case
			room = event.getData();
			System.out.println("roomName is " + room.getName());
			this.roomId = room.getId();
			System.out.println("id is " + roomId);
			warpClient.joinRoom(roomId);
		} else {
			warpClient.disconnect();
			handleError();
		}
	}
	
	public void onGetLiveRoomInfo(LiveRoomInfoEvent event){
		String[] liveUsers = event.getJoinedUsers();
		if (liveUsers!=null){
			log("onGetLiveRoomInfo: "+liveUsers.length);
		} else {
			log("onGetLiveRoomInfo: No users");
//			warpClient.deleteRoom(roomId);
		}
		WarpController.liveUsers = liveUsers;
		setWaitflag(true);
//		if(liveUsers!=null){
//			if(liveUsers.length==2){
//				startGame();
//			}else{
//				waitForOtherUser();
//			}
//		}else{
//			warpClient.disconnect();
//			handleError();
//		}
	}
	
	public void onUserJoinedRoom(String roomId, String userName){
		log("onUserJoinRoom "+userName+" joined room "+roomId);
//		if(localUser.equals(userName)==false){
//			startGame();
//		}
	}

	public void onSendChatDone(boolean status){
		log("onSendChatDone: "+status);
	}
	
	public void onGameUpdateReceived(String message){
//		log("onMoveUpdateReceived: message"+ message );
		String userName = message.substring(0, message.indexOf("#@"));
		String data = message.substring(message.indexOf("#@")+2, message.length());
		if(!localUser.equals(userName)){
			warpListener.onGameUpdateReceived(data);
		}
	}
	
	public void onResultUpdateReceived(String userName, int code){
		if(localUser.equals(userName)==false){
			STATE = FINISHED;
			warpListener.onGameFinished(code, true);
		}else{
			warpListener.onGameFinished(code, false);
		}
	}
	
	public void onUserLeftRoom(String roomId, String userName){
		log("onUserLeftRoom "+userName+" left room "+roomId);
//		if(STATE==STARTED && !localUser.equals(userName)){// Game Started and other user left the room
//			warpListener.onGameFinished(ENEMY_LEFT, true);
//		}
	}

	public void onGetUserInfo(LiveUserInfoEvent event){
		String status = event.getCustomData();
		String user = event.getName();
		if (!statusMap.containsKey(user)){
			statusMap.put(user,status);
		} else if (!statusMap.get(user).equals(status)) {
			statusMap.put(user,status);
		}
		setWaitflag(true);
	}
	
	public int getState(){
		return this.STATE;
	}
	
	private void log(String message){
		if(showLog){
			System.out.println(message);
		}
	}
	
	private void startGame(){
		STATE = STARTED;
		warpListener.onGameStarted("Start the Game");
	}
	
	private void waitForOtherUser(){
		STATE = WAITING;
		warpListener.onWaitingStarted("Waiting for other user");
	}
	
	private void handleError(){
		if(roomId!=null && roomId.length()>0){
			warpClient.deleteRoom(roomId);
		}
		System.out.println("Disconnect");
		disconnect();
	}
	
	public void handleLeave(){
		warpClient.getLiveRoomInfo(roomId);
		while (!waitflag){};
		setWaitflag(false);
		System.out.println("wait done in handle leave");
//		roomId = null;
//		room = null;

//		if(isConnected){
//			warpClient.unsubscribeRoom(roomId);
//			warpClient.leaveRoom(roomId);
//			if(STATE!=STARTED){
//				warpClient.deleteRoom(roomId);
//			}
//			warpClient.disconnect();
//		}
	}
	
	private void disconnect(){
		warpClient.removeConnectionRequestListener(new ConnectionListener(this));
		warpClient.removeChatRequestListener(new ChatListener(this));
		warpClient.removeZoneRequestListener(new ZoneListener(this));
		warpClient.removeRoomRequestListener(new RoomListener(this));
		warpClient.removeNotificationListener(new NotificationListener(this));
		warpClient.disconnect();
	}

	public static void setRoomDatas(RoomData[] roomDatas) {
		WarpController.roomDatas = roomDatas;
		WarpController.setWaitRoomFlag(true);
	}

	public static RoomData[] getRoomDatas() {
		return roomDatas;
	}

	public static void setInstance(WarpController instance) {
		WarpController.instance = instance;
	}

	public static RoomData getRoom() {
		return room;
	}

	public static String getRoomId() {
		return roomId;
	}

	public static String getLocalUser() {
		return localUser;
	}

	public static String[] getLiveUsers() {
		return liveUsers;
	}

	public static void clearLiveUsers() {
		System.out.println("Clear LiveUsers");
		liveUsers = null;
	}

	public static boolean isWaitflag() {
		return waitflag;
	}

	public static boolean isStatusflag() {
		return statusflag;
	}

	public static boolean isWaitRoomFlag() {
		return waitRoomFlag;
	}

	public static void setWaitflag(boolean waitflag) {
		WarpController.waitflag = waitflag;
	}

	public static void setStatusflag(boolean statusflag) {
		WarpController.statusflag = statusflag;
	}

	public static void setWaitRoomFlag(boolean waitRoomFlag) {
		WarpController.waitRoomFlag = waitRoomFlag;
	}

	public static HashMap<String, String> getStatusMap() {
		return statusMap;
	}

	public static void setLabelChat(Label labelChat) {
		WarpController.labelChat = labelChat;
	}

	public static void addChat(String message) {

		String textHistory = labelChat.getText().toString();
		labelChat.setText(textHistory + message + "\n");
//		if (textHistory != null){
//			labelChat.setText(textHistory + "\n" + message);
//		} else {
//			labelChat.setText(message);
//		}
	}
}
