package com.aperotechnologies.aftrparties;

/**
 * Created by hasai on 09/05/16.
 */
public class Trial
{

//    String FBId = "Meghana";
//    Configuration_Parameter m_config = Configuration_Parameter.getInstance();
//    String TAG = "";
//
//    //** Inserting Data in Usertable**/
//    public void createUser()
//    {
//        try {
//
//            UserTable selUserData = m_config.mapper.load(UserTable.class, FBId);
//            Log.e("selUserClass", " " + selUserData);
//            if (selUserData == null) {
//                UserTable user = new UserTable();
//                Log.e(TAG, "Inserting User");
//                user.setFBId(FBId);
//                user.setLogin("No");
//                user.setUserId("1001");
//                user.setLinkedInId("000");
//                user.setUserName("Harsha Asai");
//                user.setEmail("");
//                user.setFBLIEmail("");
//                user.setDOB("");
//                user.setAge(3);
//                user.setConnections(5);
//                user.setFriendsCount(20);
//                user.setDeviceToken("ahsdasydasd");
//                user.setGender("F");
//                user.setPhone("856456456456");
//                user.setProfilepic("Profilepic");
//                m_config.mapper.save(user);
//                Log.e(TAG, "User Inserted");
//
//                UserTable savedUserClass = m_config.mapper.load(UserTable.class, FBId);
//                if (savedUserClass.getFBId().equals(FBId)) {
//                    Log.e("---", " inserted successfully");
//                } else {
//                    Log.e("---", " not inserted");
//                }
//            } else {
//                if (selUserData.getFBId().equals(FBId)) {
//                    if (selUserData.getLogin().equals("Yes")) {
//                        Log.e("---", " Already exist " + selUserData.getLogin() + " getLogin");
//                        if (!selUserData.getUserName().equals("dasdasd")) {
//                            selUserData.setUserName("Meghana Patil");
//                        }
//                        if (!selUserData.getProfilepic().equals("Profilepic1")) {
//                            selUserData.setProfilepic("ChangeImage");
//                        }
//                        m_config.mapper.save(selUserData);
//                        Log.e("---", " updated successfully");
//
//
//                    } else {
//                        Log.e("---", " Already exist " + selUserData.getLogin() + " getLogin");
//                        //Update he was not user he was added as a friend
//                        Log.e(TAG, " Updating User");
//                        selUserData.setLogin("Yes");
//                        selUserData.setUserId("1002");
//                        selUserData.setLinkedInId("000");
//                        selUserData.setUserName("Harsha Asai");
//                        selUserData.setEmail("");
//                        selUserData.setFBLIEmail("");
//                        selUserData.setDOB("");
//                        selUserData.setAge(3);
//                        selUserData.setConnections(5);
//                        selUserData.setFriendsCount(20);
//                        selUserData.setDeviceToken("ahsdasydasd");
//                        selUserData.setGender("F");
//                        selUserData.setPhone("856456456456");
//                        selUserData.setProfilepic("Profilepic");
//                        m_config.mapper.save(selUserData);
//                        Log.e(TAG, "User updated");
//
//                        UserTable savedUserClass = m_config.mapper.load(UserTable.class, FBId);
//                        if (savedUserClass.getFBId().equals(FBId)) {
//                            Log.e("---", " updated successfully");
//                        } else {
//                            Log.e("---", " not updated");
//                        }
//
//                    }
//
//                }
//            }
//
//
//        } catch (Exception ex) {
//            Log.e(TAG, "Error retrieving data");
//            ex.printStackTrace();
//        }
//    }
//
//
//    /**insert data in party table*/
//
//    String flagCheckParty = "No";
//    String HostId = "Meghana";
//    String PartyId = "111";
//
//    public void createParty() {
//
//        try {
//            UserTable selUserTable = m_config.mapper.load(UserTable.class, HostId);
//            List PartyIdStatusList = new ArrayList();
//            PartyIdStatusList = selUserTable.getPartyIdStatus();
//
//            if (selUserTable.getPartyIdStatus() != null) {
//                PartyIdStatusClass PartyIdStatus = new PartyIdStatusClass();
//                for (int j = 0; j < PartyIdStatusList.size(); j++) {
//                    PartyIdStatus = (PartyIdStatusClass) PartyIdStatusList.get(j);
//                    if (PartyIdStatus.getPartyStatus().equals("Pending") || PartyIdStatus.getPartyStatus().equals("Approved") || PartyIdStatus.getPartyStatus().equals("Created")) {
////                if(PartyStartTime lies between result party start and endtime ){
//                        Log.e("---", " already created a party or exist in a party ");
//                        flagCheckParty = "Yes";
////                }else{
////                    flagCheckParty = "No";
////                }
//                    }
//                }
//            }
//
//
//            if (flagCheckParty.equals("Yes")) {
//                Log.e("---", " ---already created a party or exist in a party2--- ");
//            } else {
//                PartyTable party = new PartyTable();
//                Log.e("MainActivity---", "Inserting party");
//                party.setPartyId(PartyId);
//                party.setPartyName("");
//                party.setPartyStartTime("");
//                party.setPartyEndTime("");
//                party.setDate("");
//                party.setHostFBID("");
//                party.setHostUserId("");
//                party.setHostName("");
//                party.setPredefineDesc("");
//                party.setDescription("");
//                party.setBYOB("");
//                party.setPartyAddress("");
//                party.setLatLong("");
//                party.setPartyImage("");
//                party.setFlagMask("");
//                //party.setDialogId("");
//                m_config.mapper.save(party);
//                Log.e("MainActivity---", "Party inserted");
//                updateUserTable();
//
////
//            }
//
//        } catch (Exception ex) {
//            Log.e(TAG, "Error retrieving data");
//            ex.printStackTrace();
//        }
//    }
//
//
//
//
//
//
//    /** UserTable updation at time of creating party**/
//    public void updateUserTable() {
//        try {
//
//            UserTable selectedUser = m_config.mapper.load(UserTable.class, FBId);
//            Log.e("selectedUser", " " + selectedUser);
//            Log.e("getPartyIdStatus", " " + selectedUser.getPartyIdStatus());
//            if (selectedUser.getPartyIdStatus() == null) {
//                PartyIdStatusClass PartyIdstatus = new PartyIdStatusClass();
//                PartyIdstatus.setPartyId("Party 1");
//                PartyIdstatus.setPartyStatus("Created");
//                PartyIdstatus.setStartTime("2016-04-29T13:10:57+05:30");
//                PartyIdstatus.setEndTime("2016-04-29T13:10:57+05:30");
//                List userPartyIdStatusList = new ArrayList();
//                userPartyIdStatusList.add(PartyIdstatus);
//                selectedUser.setPartyIdStatus(userPartyIdStatusList);
//                m_config.mapper.save(selectedUser);
//            } else {
//                //add new entry to existing array
//                PartyIdStatusClass PartyIdstatus = new PartyIdStatusClass();
//                PartyIdstatus.setPartyId("2");
//                PartyIdstatus.setPartyStatus("Declined");
//                PartyIdstatus.setStartTime("2016-04-29T13:10:57+05:30");
//                PartyIdstatus.setEndTime("2016-04-29T13:10:57+05:30");
//
//                List userPartyIdStatusList = new ArrayList();
//                userPartyIdStatusList = selectedUser.getPartyIdStatus();
//                Log.e("finalpartyIdstatus size", " " + userPartyIdStatusList.size());
//                userPartyIdStatusList.add(PartyIdstatus);
//                selectedUser.setPartyIdStatus(userPartyIdStatusList);
//                m_config.mapper.save(selectedUser);
//
//            }
//
//
//        } catch (Exception ex) {
//            Log.e(TAG, "Error Update retrieving data");
//            ex.printStackTrace();
//        }
//    }
//
//
//
//    /** PartyTable updation at time of creating party**/
//    public void updatePartyTable() {
//        try {
//
//            PartyTable selectedParty = m_config.mapper.load(PartyTable.class, PartyId);
//            Log.e("selectedParty", " " + selectedParty);
//            Log.e("getUserIdStatus", " " + selectedParty.getUserIdStatus());
//            if (selectedParty.getUserIdStatus() == null) {
//                UserIdStatusClass UserIdStatus = new UserIdStatusClass();
//                UserIdStatus.setUserId("434543");
//                UserIdStatus.setUserPartyStatus("Created");
//                UserIdStatus.setUserGoingStatus("No");
//
//                List userIdStatusList = new ArrayList();
//                userIdStatusList.add(UserIdStatus);
//                selectedParty.setUserIdStatus(userIdStatusList);
//                m_config.mapper.save(selectedParty);
//            } else {
//                //add new entry to existing array
//                UserIdStatusClass UserIdStatus = new UserIdStatusClass();
//                UserIdStatus.setUserId("434543");
//                UserIdStatus.setUserPartyStatus("Created");
//                UserIdStatus.setUserGoingStatus("No");
//
//                List userIdStatusList = new ArrayList();
//                userIdStatusList = selectedParty.getUserIdStatus();
//                Log.e("finalpartyIdstatus size", " " + userIdStatusList.size());
//                userIdStatusList.add(UserIdStatus);
//                selectedParty.setUserIdStatus(userIdStatusList);
//                m_config.mapper.save(selectedParty);
//
//            }
//
//
//        } catch (Exception ex) {
//            Log.e(TAG, "Error Update retrieving data");
//            ex.printStackTrace();
//        }
//    }

}



//store obj data in gson sharedpreference

//                 Gson gson = new Gson();
//                String qbuserjson = gson.toJson(user);
//                m_config.qb_user = user;
//                SharedPreferences.Editor editor = sharedpreferences.edit();
//                editor.putString(m_config.qbuser, qbuserjson);
//                editor.apply();


