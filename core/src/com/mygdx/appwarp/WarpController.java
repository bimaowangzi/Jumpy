package com.mygdx.appwarp;


import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.mygdx.Screens.LobbyScreen;
import com.shephertz.app42.gaming.multiplayer.client.WarpClient;
import com.shephertz.app42.gaming.multiplayer.client.command.WarpResponseResultCode;
import com.shephertz.app42.gaming.multiplayer.client.events.LiveRoomInfoEvent;
import com.shephertz.app42.gaming.multiplayer.client.events.LiveUserInfoEvent;
import com.shephertz.app42.gaming.multiplayer.client.events.RoomData;
import com.shephertz.app42.gaming.multiplayer.client.events.RoomEvent;

import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;

/**WarpController is a Hub for managing the Appwarp listeners and
 * Callbacks from the listeners will usually return to the WarpController*/
public class WarpController {

	private static WarpController instance;
	
	private boolean showLog = true;

//	Lam key for demo
	private final String apiKey = "cdf14cd469b67cd707160f7e970d74689f56df96210c9c145c9474ce046bde37";
	private final String secretKey = "297355b0c132a2eaba9b303f99e6ddc0fc8d5a2ad9d5de16b60eecdfe3168efd";

//	Daniel key for testing
//	private final String apiKey = "6f5c7b262df95f8bf047efc74909486e72c2aa2fad8503057c6fc25efae3f141";
//	private final String secretKey = "770d99236a7c4ee4a54bf9b8db1653f521c4899e3ffe02a8f79eb091c84ed272";
	
	private WarpClient warpClient;

	private static String localUser;
	private static String roomId;
	private static String[] liveUsers;

	private static ConcurrentHashMap<String,String> statusMap = new ConcurrentHashMap<String,String>();
	private static ConcurrentHashMap<String,String> avatarMap = new ConcurrentHashMap<String,String>();

	private static String data;
	public static volatile boolean dataAvailable = false;


	private boolean isConnected = false;
	boolean isUDPEnabled = false;

	private static RoomData[] roomDatas = null;
	private static RoomData room;

	private static Label labelChat;

	private static int start;
	private static int interval;

	/**Flags are use to synchronize the Appwarp with the game*/
	private static volatile boolean waitflag = false;
	private static volatile boolean waitRoomFlag = false;
	private static volatile boolean statusflag = false;
	private static volatile boolean deleteFlag = false;

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

	/**Called at upon login to connect with username*/
	public void startApp(String localUser){
		this.localUser = localUser;
		warpClient.connectWithUserName(localUser);
	}

	/**Initialize Appwarp*/
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
			/** Join room is nested in subscribe loop
			 We want to always subscribe before we join room
			 Although it may not be that necessary for creating room
			 But it is a good practice*/
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
		WarpController.liveUsers = liveUsers;
		setWaitflag(true);
	}
	
	public void onUserJoinedRoom(String roomId, String userName){
		log("onUserJoinRoom "+userName+" joined room "+roomId);
		try {
			if (WarpController.getLiveUsers()[0].equals(WarpController.getLocalUser())){
				HashMap<String,Object> roomProperties = new HashMap<String,Object>();
				roomProperties.put("start", LobbyScreen.startSeed);
				roomProperties.put("interval",LobbyScreen.intervalSeed);
				WarpClient.getInstance().updateRoomProperties(WarpController.getRoomId(),roomProperties,null);
			}
			System.out.println("update " + userName + " room property");
		} catch (Exception ex){
			ex.printStackTrace();
		}
	}

	public void onSendChatDone(boolean status){
		log("onSendChatDone: "+status);
	}
	
	public void onGameUpdateReceived(String message) {
		String userName = message.substring(0, message.indexOf("#@"));
		if (userName.equals(getLocalUser()))
			return;

		data = message;
		dataAvailable = true;
	}
	
	public void onRoomUpdateReceived(Integer start, Integer interval){
		WarpController.start = start;
		WarpController.interval = interval;
	}
	
	public void onUserLeftRoom(String roomId, String userName){
		log("onUserLeftRoom "+userName+" left room "+roomId);
	}

	public void onGetUserInfo(LiveUserInfoEvent event){
		if (event != null){
			String[] customData = event.getCustomData().split(",");
			String status = event.getCustomData();
			String avatar = "none";

			try {
				status = customData[0];
				avatar = customData[1];
			}
			catch(ArrayIndexOutOfBoundsException e){
				e.printStackTrace();
			}
			String user = event.getName();
			statusMap.put(user,status);

			if (!avatarMap.containsKey(user)){
				avatarMap.put(user,"none");
			} else
				avatarMap.put(user,avatar);
		} else {
			System.out.println("user event is null");
		}

		setWaitflag(true);
	}
	
	private void log(String message){
		if(showLog){
			System.out.println(message);
		}
	}
	
	private void handleError(){
		if(roomId!=null && roomId.length()>0){
			warpClient.deleteRoom(roomId);
		}
		System.out.println("Disconnect");
		disconnect();
	}
	
	public void disconnect(){
		warpClient.removeConnectionRequestListener(new ConnectionListener(this));
		warpClient.removeChatRequestListener(new ChatListener(this));
		warpClient.removeZoneRequestListener(new ZoneListener(this));
		warpClient.removeRoomRequestListener(new RoomListener(this));
		warpClient.removeNotificationListener(new NotificationListener(this));
		warpClient.disconnect();
		setWaitflag(true);
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

	public static boolean isDeleteFlag() {
		return deleteFlag;
	}

	public static int getStart() {
		return start;
	}

	public static int getInterval() {
		return interval;
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

	public static void setDeleteFlag(boolean deleteFlag) {
		WarpController.deleteFlag = deleteFlag;
	}

	public static ConcurrentHashMap<String, String> getStatusMap() {
		return statusMap;
	}

	public static void clearStatusMap() { statusMap.clear(); }

	public static void clearAvatarMap() { avatarMap.clear(); }

	public static ConcurrentHashMap<String, String> getAvatarMap() {
		return avatarMap;
	}

	public static void setLabelChat(Label labelChat) {
		WarpController.labelChat = labelChat;
	}

	public static void addChat(String message) {

		String textHistory = labelChat.getText().toString();
		labelChat.setText(textHistory + message + "\n");
	}

	public static String getData() {
		return data;
	}
}
