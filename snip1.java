        public void ProcessLine(dynamic line)
        {

            //For certain events we want to read _all the time_, for other events, make sure they happen AFTER we launched the application.
            switch ((string)line.eventtype)
            {
                //These three are only called at the time the game fires up or a new log is generated, you get the idea.
                case "Fileheader":
                    //The game has created a new journal file, or the application has just been started.
                    //Stop checking the journal.
                    timer1.Enabled = false;

                    //Make sure we have no pending missions, we could have accepted missions, closed the game, and re-entered.
                    PreCheckUpdate(false);

                    if (line.gameversion.ToUpper().Contains("BETA"))
                    {
#if DEBUG
                        DisplayHtml("Current Journal File is for the beta, however, JTracker is in debug mode so will continue.", "1");
#else
                        //No further tracking.
                        Globals.journalTracking = false;
                        DisplayHtml("Current Journal File (" + Globals.journalDir + "/" + Globals.journalFile + ") is for the beta, tracking disabled until a live journal file is found.", "1");
#endif
                    }
                    
                    //Ok, continue checking journal.
                    timer1.Enabled = true;
                    break;
                case "Loadout":
                    if (line.Ship != null)
                    {
                        if (line.Ship == "") line.Ship = "Unknown";
                        Globals.curship = line.Ship;
                    }
                    if (line.ShipName != null)
                    {
                        if (line.ShipName == "") line.ShipName = "[UNNAMED]";
                        Globals.curshipname = line.ShipName;
                    }
                    if (line.ShipIdent != null)
                    {
                        if (line.ShipIdent == "") line.ShipIdent = "NO ID";
                        Globals.curshipident = line.ShipIdent;
                    }

                    UpdateHeaderLabels();
                    break;
                case "LoadGame":
                    Globals.cmdr = line.Commander;
                    Globals.curmode = line.GameMode;
                    lblCmdrSystemStation.Text = line.Commander;

                    if (line.Group != null)
                    {
                        Globals.curgroup = line.Group;
                        lblMode.Text = "Mode: " + line.GameMode + " (" + line.Group + ")";
                    }else
                    {
                        Globals.curgroup = "";
                        lblMode.Text = "Mode: " + line.GameMode;
                    }

                    if (line.Ship != null)
                    {
                        if (line.Ship == "") line.Ship = "Unknown";
                        Globals.curship = line.Ship;
                    }
                    if (line.ShipName != null)
                    {
                        if (line.ShipName == "") line.ShipName = "[UNNAMED]";
                        Globals.curshipname = line.ShipName;
                    }
                    if (line.ShipIdent != null)
                    {
                        if (line.ShipIdent == "") line.ShipIdent = "NO ID";
                        Globals.curshipident = line.ShipIdent;
                    }

                    UpdateHeaderLabels();

                    //Got the cmdr. Now check permissions.
                    if (wbBGS.Url.AbsoluteUri == "about:blank"){
                        if (wbBGS.IsBusy)
                        {
                            wbBGS.Stop();
                        }
                        
                        wbBGS.Navigate("http://tracker.eicgaming.com/permission.php?CMDR=" + Globals.cmdr + "&Version=" + Globals.version);
                    }
                    

                    /*
                    if(notifyIcon.Visible == true)
                    {
                        notifyIcon.BalloonTipTitle = "Game Mode Changed";
                        notifyIcon.BalloonTipText = line.GameMode;
                        notifyIcon.ShowBalloonTip(60000);
                    }
                    */
                    break;
                case "Location":
                    UpdateSystem(line.StarSystem, line.StarPos[0], line.StarPos[1], line.StarPos[2], line.SystemFaction);
                    if (line.Docked == "true")
                    {
                        UpdateStation(line.StationName, ""); //The location event does not have a StationFaction presented, but if Docked, the Docked event follows immediately after which does.
                    }

                    if (line.timestamp > Globals.curtime) //If this is a current record (FSDJump processes all the time)
                    {
                        //Update the tracker with the system control faction.
                        if (line.SystemFaction != null)
                        {
                            if (line.Factions != null)
                            {
                                if (line.Factions.Count > 0)
                                {
                                    var Security = "";
                                    if (line.SystemSecurity != null) Security = ReplaceString(line.SystemSecurity);
                                    TrackData(line.StarSystem, "INFLUENCEFULL", 0, JsonConvert.SerializeObject(line.Factions), ReplaceString(line.SystemFaction), ReplaceString(Security));
                                }
                            }else
                            {
                                //No Factions :(
                                TrackData(line.StarSystem, "SYSTEM", 0, line.SystemFaction);
                            }

                        }

                        if (line.Population != null)
                        {
                            TrackData(line.StarSystem, "POPULATION", 0, Convert.ToString(line.Population));
                        }

                        //To be added into the journal: Faction Influence + States for the entire system.
                        if (line.Powers != null)
                        {
                            string Power = "";
                            switch ((int)line.Powers.Count)
                            {
                                case 0:
                                    break;
                                case 1:
                                    //This is ideal, one power.
                                    foreach (var PowerPlayer in line.Powers)
                                    {
                                        Power = PowerPlayer;
                                    }
                                    break;
                                default: //More than 1, not ideal.
                                    Power = "Multiple";
                                    break;
                            }
                            string PowerState = "";
                            if (line.PowerplayState != null) PowerState = line.PowerplayState;
                            TrackData(line.StarSystem, "POWERPLAY", 0, Power, PowerState, JsonConvert.SerializeObject(line.Powers));
                        }
                    }

                    break;

                //Docked at a station.
                case "Docked":
                    UpdateStation(line.StationName, line.StationFaction);
                    if (line.timestamp > Globals.curtime) //If this is a current record (Docked processes all the time)
                    {
                        if (line.StationFaction != null)
                        {
                            //Update the tracker with the station control faction.
                            TrackData(line.StarSystem, "STATION", 0, line.StationFaction, line.StationName);
                        }
                    }
                    break;
                //Aproaching a settlement, so let's make sure theres an entry in the database.
                case "ApproachSettlement":
                    TrackData(Globals.cursystem.ToUpper(), "SETTLEMENT", 0, "", line.Name);
                    break;
                //Undocked at a station
                case "Undocked":
                    UpdateStation("", "");
                    break;
                //Entered Supercruise.... no change in system nor station.
                case "SupercruiseEntry":
                    break;
                //Exited Supercruise.... no change in system nor station (yet).
                case "SupercruiseExit":
                    break;
                //Jumped to another system, update the Current system.
                case "FSDJump":
                    UpdateSystem(line.StarSystem, line.StarPos[0], line.StarPos[1], line.StarPos[2], line.SystemFaction);
#if DEBUG
                    if (true)
#else
                    if (line.timestamp > Globals.curtime) //If this is a current record (FSDJump processes all the time)
#endif
                    { 
                        //Update the tracker with the system control faction.
                        if (line.SystemFaction != null)
                        {
                            //To be added into the journal: Faction Influence + States for the entire system.
                            if (line.Factions != null)
                            {
                                if (line.Factions.Count > 0)
                                {
                                    var Security = "";
                                    if (line.SystemSecurity != null) Security = ReplaceString(line.SystemSecurity);
                                    TrackData(line.StarSystem, "INFLUENCEFULL", 0, JsonConvert.SerializeObject(line.Factions), ReplaceString(line.SystemFaction), ReplaceString(Security));
                                }
                            }else
                            {
                                //No Factions :(
                                TrackData(line.StarSystem, "SYSTEM", 0, line.SystemFaction);
                            }
                        }

                        if(line.Population != null)
                        {
                            TrackData(line.StarSystem, "POPULATION", 0, Convert.ToString(line.Population));
                        }

                        
                        if(line.Powers != null)
                        {
                            string Power = "";
                            switch ((int)line.Powers.Count)
                            {
                                case 0:
                                    break;
                                case 1:
                                    //This is ideal, one power.
                                    foreach (var PowerPlayer in line.Powers)
                                    {
                                        Power = PowerPlayer;
                                    }
                                    break;
                                default: //More than 1, not ideal.
                                    Power = "Multiple";
                                    break;
                            }
                            string PowerState = "";
                            if (line.PowerplayState != null) PowerState = line.PowerplayState;
                            TrackData(line.StarSystem, "POWERPLAY", 0, Power, PowerState, JsonConvert.SerializeObject(line.Powers));
                        }
                    }
                    break;
                default:
                    //All the rest of the events have to check that they're happening AFTER the app was launched to ensure we don't back-check the journal.
                    
#if DEBUG
                        if (true)
                        //if (line.timestamp > Globals.curtime)
#else
                        if (line.timestamp > Globals.curtime)
                        //if (true)
#endif
                        {
                        switch ((string)line.eventtype)
                        {
                            case "ReceiveText": //We actually aren't interested in what the player says to us.
                                break;
                            case "SendText": //But if we say our code word to another player, IFF them.
                                var codeword = Properties.Settings.Default.IFFText;
                                if (codeword != "") //We have a code word set!
                                {
                                    if (line.Message.Contains(codeword)) //The message contains our code word! Oh goody!
                                    {
                                        //Who is either line.To_Localised in format "CMDR Cazz0r", or simply line.To in format "Cazz0r"
                                        var Who = "";
                                        if (line.To_Localised != null)
                                        {
                                            Who = line.To_Localised.Substring(5);
                                        }
                                        else if (line.To != null)
                                        {
                                            if (line.To.ToUpper() != "LOCAL" && line.To.ToUpper() != "WING")
                                            {
                                                Who = line.To;
                                            }
                                        }
                                        if (Who != "")
                                        {
                                            txtIFF.Text = Who;
                                            if (Properties.Settings.Default.IFFAuto)
                                            {
                                                FindCommander();
                                            }
                                        }
                                    }
                                }
                                
                                break;
                            case "JoinACrew":
                                txtIFF.Text = line.Captain;
                                if (Properties.Settings.Default.IFFAuto)
                                {
                                    FindCommander();
                                }
                                break;
                            case "Interdiction":
                                if (line.IsPlayer == true)
                                {
                                    //We've interdicted a player. Let's do an IFF on the line.Interdicted 
                                    txtIFF.Text = line.Interdicted;
                                    if (Properties.Settings.Default.IFFAuto)
                                    {
                                        FindCommander();
                                    }
                                }
                                break;
                            case "EscapeInterdiction":
                            case "Interdicted":
                                if(line.IsPlayer == true)
                                {
                                    //We've been interdicted, let's do an IFF on the line.Interdictor
                                    txtIFF.Text = line.Interdictor;
                                    if (Properties.Settings.Default.IFFAuto)
                                    {
                                        FindCommander();
                                    }
                                }
                                break;
                            case "Bounty": //Occurs when a player kills a ship
                                {
                                    TrackData(Globals.cursystem, "ShipKill", 1, line.VictimFaction.ToString(), line.TotalReward.ToString(), line.Target.ToString());
                                }
                                break;
                            case "SellExplorationData":
                                //For the Current system: +BaseValue to ExplorationClaimed.
                                DataRow ExploreSystem = dataSystems.Tables["StarSystems"].Rows.Find(Globals.cursystem.ToUpper());
                                if (ExploreSystem == null)
                                {
                                    DataRow exploreSystem = dataSystems.Tables["StarSystems"].NewRow();
                                    exploreSystem["SystemName"] = Globals.cursystem.ToUpper();
                                    exploreSystem["ExplorationClaimed"] = line.BaseValue + line.Bonus;
                                    dataSystems.Tables["StarSystems"].Rows.Add(exploreSystem);
                                }
                                else
                                {
                                    ExploreSystem["ExplorationClaimed"] = Convert.ToInt64(ExploreSystem["ExplorationClaimed"]) + Convert.ToInt64(line.BaseValue) + Convert.ToInt64(line.Bonus);
                                }

                                //Track the selling of exploration data
                                TrackData(Globals.cursystem.ToUpper(), "Exploration", (Convert.ToInt64(line.BaseValue) + Convert.ToInt64(line.Bonus)), Globals.curstationfaction);

                                //See if we've just Discovered any new systems
                                //dynamic d = JsonConvert.DeserializeObject<JEvent>(line.Systems);
                                if(line.Discovered.Count > 0)
                                {
                                    btnDiscoveries.Visible = true;


                                    //We've discovered some systems!
                                    foreach (var sys in line.Discovered)
                                    {
                                        DataRow DiscoverSystem = dataSystems.Tables["Discoveries"].NewRow();
                                        DiscoverSystem["BodyName"] = sys;
                                        dataSystems.Tables["Discoveries"].Rows.Add(DiscoverSystem);
                                    }

                                    /*

                                    string postData = "Discovered=" + line.Discovered;
                                    System.Text.Encoding encoding = System.Text.Encoding.UTF8;
                                    byte[] bytes = encoding.GetBytes(postData);
                                    string url = "http://tracker.eicgaming.com/exploration.php?CMDR=" + Globals.cmdr;
                                    wb.Navigate(url, string.Empty, bytes, "Content-Type: application/x-www-form-urlencoded");
                                    
                                   //*/
                                }
                                break;
                            case "MissionAccepted":
                                //Record the mission ID + accepted system
                                DataRow newMission = dataSystems.Tables["Missions"].NewRow();
                                newMission["MissionID"] = line.MissionID;
                                newMission["AcceptedIn"] = Globals.cursystem.ToUpper();
                                newMission["MissionFromFaction"] = line.Faction;
                                newMission["MissionName"] = line.Name;

                                if (line.Influence == null)
                                {
                                    newMission["InfluenceEffect"] = "Med"; //Medium until the beta goes live and we're finally given the influence effect in the tracker.
                                }
                                else
                                {
                                    newMission["InfluenceEffect"] = line.Influence;
                                }

                                //Determine the mission type.
                                //If this mission is a find x of y and bring them back we'll have a commodity to work with.
                                line.Type = "UNKNOWN";
                                var TargetType = "";
                                //if (line.Commodity_Localised != null)
                                {
                                    /* 

                                    // Mission Names:
                                    Mission_Delivery_Boom: Courier
                                    Mission_Courier_Boom: Courier

                                    Mission_AltruismCredits: Donation

                                    // Passenger Mission Names:
                                    Mission_PassengerVIP_Criminal_BOOM: Courier
                                    Mission_PassengerVIP_General_WAR: Courier
                                    Mission_PassengerVIP_Scientist_WAR: Courier

                                    */
                                    if (line.Name.ToUpper().Contains("CREDITS")) //Credit Donate / Credit blah
                                    {
                                        line.Type = "DONATION";
                                        line.DestinationSystem = Globals.cursystem;
                                        //Never suffers redirects, but complete event also contains faction.
                                    }
                                    else if (line.Name.ToUpper().Contains("COLLECT") || line.Name.ToUpper().Contains("DONATE")) //Didn't have credit in the title, we're donating something else.
                                    {
                                        line.Type = "COLLECT";
                                        //Is subject to mission redirects to alternate system AND FACTION, by RNG. Complete event contains faction.
                                    }
                                    else if (line.Name.ToUpper().Contains("PASSENGER"))
                                    {
                                        line.Type = "PASSENGER";
                                        //Is subject to mission redirects to alternate system AND FACTION, by RNG. Complete event contains faction.
                                    }
                                    else if (line.Name.ToUpper().Contains("DELIVERY") || line.Name.ToUpper().Contains("COURIER"))
                                    {
                                        line.Type = "DELIVERY";
                                        //Is subject to mission redirects to alternate system AND FACTION, by nature. Complete event contains faction.
                                    }
                                    else if (line.Name.ToUpper().Contains("SIGHTSEEING"))
                                    {
                                        line.Type = "SIGHTSEEING";
                                        line.DestinationSystem = Globals.cursystem;
                                        //Not sure on redirects, but is highly likely, complete event contains faction.
                                    }
                                    else if (line.Name.ToUpper().Contains("MASSACRE"))
                                    {
                                        line.Type = "MASSACRE";
                                        line.DestinationSystem = Globals.cursystem;

                                        //Check if Target Faction exists.
                                        if (line.TargetFaction == null)
                                        {
                                            line.TargetFaction = "Undefined";
                                        }

                                        if(line.TargetCount == null)
                                        {
                                            line.TargetCount = 0;
                                        }
                                        if(line.KillCount != null)
                                        {
                                            line.TargetCount = line.KillCount;
                                        }

                                        
                                        if (line.Name.ToUpper().Contains("SKIMMER"))
                                        {
                                            TargetType = "Skimmers";
                                        }else
                                        {
                                            TargetType = "Ships";
                                        }

                                            //Not sure on redirects, but is highly unlikely, complete event contains faction.
                                            newMission["TargetCount"] = line.TargetCount;
                                        newMission["TargetFaction"] = line.TargetFaction;
                                    }
                                    else if (line.Name.ToUpper().Contains("LONGDISTANCEEXPEDITION"))
                                    {
                                        //Not sure on redirects, but is highly likely, complete event contains faction.
                                        line.Type = "EXPEDITION";
                                        line.DestinationSystem = Globals.cursystem;
                                    }
                                    else if (line.Name.ToUpper().Contains("ASSASSINATE"))
                                    {
                                        line.Type = "ASSASSINATION";
                                    }
                                }

                                if (line.DestinationSystem == null)
                                {
                                    line.DestinationSystem = Globals.cursystem;
                                }

                                newMission["DestinationSystem"] = line.DestinationSystem.ToUpper();
                                newMission["MissionType"] = line.Type;
                                dataSystems.Tables["Missions"].Rows.Add(newMission);

                                if (line.Type == "MASSACRE")
                                {
                                    DataRow newOverlayMission = dataSystems.Tables["Overlay"].NewRow();
                                    newOverlayMission["MissionID"] = line.MissionID;
                                    newOverlayMission["Type"] = line.Type;
                                    newOverlayMission["DestinationSystem"] = line.DestinationSystem;
                                    newOverlayMission["TargetFaction"] = line.TargetFaction;
                                    newOverlayMission["TargetCount"] = line.TargetCount;
                                    newOverlayMission["TargetType"] = TargetType;
                                    newOverlayMission["TargetCountKilled"] = 0;
                                    newOverlayMission["ForFaction"] = line.Faction;
                                    dataSystems.Tables["Overlay"].Rows.Add(newOverlayMission);
                                    OverlayMissions();
                                }

                                //Record the system we picked up the mission in.
                                DataRow foundrow = dataSystems.Tables["StarSystems"].Rows.Find(Globals.cursystem.ToUpper());
                                if (foundrow == null)
                                {
                                    DataRow newSystem = dataSystems.Tables["StarSystems"].NewRow();
                                    newSystem["SystemName"] = Globals.cursystem.ToUpper();
                                    newSystem["MissionsPending"] = 1;
                                    newSystem["MissionsDestination"] = 0;
                                    newSystem["MissionsComplete"] = 0;
                                    dataSystems.Tables["StarSystems"].Rows.Add(newSystem);
                                }
                                else
                                {
                                    var x = Convert.ToInt64(foundrow["MissionsPending"]);
                                    var y = x + 1;
                                    foundrow["MissionsPending"] = y.ToString();

                                }

                                //Record the destination system.
                                DataRow foundrow2 = dataSystems.Tables["StarSystems"].Rows.Find(line.DestinationSystem.ToUpper());
                                if (foundrow2 == null)
                                {
                                    DataRow newSystem = dataSystems.Tables["StarSystems"].NewRow();
                                    newSystem["SystemName"] = line.DestinationSystem.ToUpper();
                                    newSystem["MissionsPending"] = 0;
                                    newSystem["MissionsDestination"] = 1;
                                    newSystem["MissionsComplete"] = 0;
                                    dataSystems.Tables["StarSystems"].Rows.Add(newSystem);
                                }
                                else
                                {
                                    var x = Convert.ToInt64(foundrow2["MissionsDestination"]);
                                    var y = x + 1;
                                    foundrow2["MissionsDestination"] = y.ToString();
                                }

                                if (line.Type == "COLLECT" && line.Commodity_Localised != null)
                                {
                                    

                                    //We need to rename some of the commodities.
                                    switch ((string)line.Commodity_Localised.ToUpper())
                                    {
                                        case "ATMOSPHERIC PROCESSORS":
                                            line.Commodity_Localised = "Atmospheric Extractors";
                                            break;
                                        case "BIOREDUCING LICHEN":
                                            line.Commodity_Localised = "Bio Reducing Lichen";
                                            break;
                                        
                                        case "ANIMALMEAT":
                                            line.Commodity_Localised = "Animal Meat";
                                            break;

                                        case "CMM COMPOSITE":
                                        case "C M M COMPOSITE":
                                        case "C.M.M. COMPOSITE":
                                            line.Commodity_Localised = "CMM Composite";
                                            break;

                                        case "HN SHOCKMOUNT":
                                        case "H N SHOCKMOUNT":
                                        case "H.N. SHOCKMOUNT":
                                            line.Commodity_Localised = "HN Shock Mount";
                                            break;

                                        case "HE SUITS":
                                        case "H E SUITS":
                                        case "H.E. SUITS":
                                            line.Commodity_Localised = "Hazardous Environment Suits";
                                            break;
                                        case "LAND ENRICHMENT SYSTEMS":
                                            line.Commodity_Localised = "Terrain Enrichment Systems";
                                            break;

                                    }
                                    //MessageBox.Show(line.Commodity_Localised);
                                    cmbCommodity.SelectedItem = line.Commodity_Localised;
                                    //MessageBox.Show(cmbCommodity.SelectedItem.ToString());
                                    //wb.Navigate("http://tracker.eicgaming.com/commodity.php?CMDR=" + Globals.cmdr + "&Commodity=" + line.Commodity_Localised + "&Station=" + Globals.curstation);
                                }


                                break;
                            case "MissionCompleted":
                                string missionreward = "0";
                                if (line.Reward != null)
                                {
                                    missionreward = (string)line.Reward;
                                }

                                DataRow foundmission = dataSystems.Tables["Missions"].Rows.Find(line.MissionID);
                                if (foundmission == null)
                                {
                                    //We never had the mission in the db, nothing we can do about that. 
                                    //We don't know where we accepted it to credit it.
                                    //We only know where we handed it in and that means nothing to us at the moment.

                                    //Version 1.0.77: Look for the MissionAccept in the historical log files up to 4 weeks back.
                                    if (FindMissionAccept(line.MissionID))
                                    {
                                        //That function would have inserted the mission into the missions table and has returned true, look for it again.
                                        foundmission = dataSystems.Tables["Missions"].Rows.Find(line.MissionID);
                                    }//Else if returned false and didn't find the missionaccepted, that's a bummer.
                                }

                                if(foundmission != null) //Either the mission was originally in the missions table or has just been inserted.
                                {
                                    //We knew about the mission since we accepted it, don't worry about the passed destination system, (it's not present for donation missions and wrong for redirected missions).
                                    foundmission["CompletedIn"] = Globals.cursystem.ToUpper(); //line.DestinationSystem.ToUpper();

                                    //For the AcceptedIn system: +1 MissionsComplete, -1 MissionsPending.
                                    DataRow foundsystem = dataSystems.Tables["StarSystems"].Rows.Find(((string)foundmission["AcceptedIn"]).ToUpper());
                                    if (foundsystem == null)
                                    {
                                        DataRow newSystem = dataSystems.Tables["StarSystems"].NewRow();
                                        newSystem["SystemName"] = ((string)foundmission["AcceptedIn"]).ToUpper();
                                        newSystem["MissionsPending"] = 0;
                                        newSystem["MissionsDestination"] = 0;
                                        newSystem["MissionsComplete"] = 1;
                                        dataSystems.Tables["StarSystems"].Rows.Add(newSystem);
                                    }
                                    else
                                    {
                                        foundsystem["MissionsComplete"] = (Convert.ToInt64(foundsystem["MissionsComplete"]) + 1).ToString();
                                        foundsystem["MissionsPending"] = (Convert.ToInt64(foundsystem["MissionsPending"]) - 1).ToString();
                                    }

                                   

                                    //Track the completed mission - All Missions are displaying as medium at the moment.
                                    TrackData(((string)foundmission["AcceptedIn"]).ToUpper(), "Mission" + (string)foundmission["InfluenceEffect"], 1, (string)foundmission["MissionFromFaction"], missionreward, (string)foundmission["MissionType"]);

                                    //Track the destination completion event,
                                    TrackData(Globals.cursystem.ToUpper(), "MissionD" + (string)foundmission["InfluenceEffect"], 1, (string)line.Faction, missionreward, (string)foundmission["MissionType"]);

                                    //For the CompletedIn system: -1 MissionsDestination. Use the original destination.
                                    DataRow foundsystem2 = dataSystems.Tables["StarSystems"].Rows.Find(foundmission["DestinationSystem"]);
                                    if (foundsystem2 == null)
                                    {
                                        //Wasn't in the DB as a destination, no need to add it really, it'll only be 0,0,0.
                                    }
                                    else
                                    {
                                        foundsystem2["MissionsDestination"] = (Convert.ToInt64(foundsystem2["MissionsDestination"]) - 1).ToString();
                                    }
                                }else
                                {
                                    //We tried to find where we accepted the mission, but no luck, just record the destination completion event, presume "medium" influence.
                                    TrackData(Globals.cursystem.ToUpper(), "MissionD", 1, (string)line.Faction, missionreward, "Unknown");
                                }
                                break;
                            case "MissionAbandoned":
                            case "MissionFailed":
                                //Oh no we failed a mission!

                                DataRow foundmission2 = dataSystems.Tables["Missions"].Rows.Find(line.MissionID);
                                if (foundmission2 == null)
                                {
                                    //We never knew about this mission, nothing to do.
                                }
                                else
                                {
                                    //We didn't complete it, we failed or abandoned it!
                                    foundmission2["CompletedIn"] = (string)line.eventtype;

                                    //For the AcceptedIn system: -1 pending.
                                    //For the AcceptedIn system: +1 MissionsComplete, -1 MissionsPending.
                                    DataRow foundsystem = dataSystems.Tables["StarSystems"].Rows.Find(((string)foundmission2["AcceptedIn"]).ToUpper());
                                    if (foundsystem == null)
                                    {
                                        //Don't bother creating it just for 0,0,0
                                    }
                                    else
                                    {
                                        foundsystem["MissionsPending"] = Convert.ToInt64(foundsystem["MissionsPending"]) - 1;
                                    }

                                    //For the destinationsystem: -1 to destination
                                    DataRow foundsystem2 = dataSystems.Tables["StarSystems"].Rows.Find(((string)foundmission2["DestinationSystem"]).ToUpper());
                                    if (foundsystem2 == null)
                                    {
                                        //Don't bother creating it just for 0,0,0
                                    }
                                    else
                                    {
                                        foundsystem2["MissionsDestination"] = Convert.ToInt64(foundsystem2["MissionsDestination"]) - 1;
                                    }

                                    //Check if the Overlay has this mission present.
                                    DataRow foundmission3 = dataSystems.Tables["Overlay"].Rows.Find(line.MissionID);
                                    if(foundmission3 != null)
                                    {
                                        foundmission3.Delete();
                                        OverlayMissions();
                                    }
                                }

                                break;

                            //Trading
                            case "MarketBuy":
                                //For the Current system: +count to traded.
                                DataRow BuySystem = dataSystems.Tables["StarSystems"].Rows.Find(Globals.cursystem.ToUpper());
                                if (BuySystem == null)
                                {
                                    DataRow newSystem = dataSystems.Tables["StarSystems"].NewRow();
                                    newSystem["SystemName"] = Globals.cursystem.ToUpper();
                                    newSystem["Traded"] = line.Count;
                                    dataSystems.Tables["StarSystems"].Rows.Add(newSystem);
                                }
                                else
                                {
                                    BuySystem["Traded"] = Convert.ToInt64(BuySystem["Traded"]) + Convert.ToInt64(line.Count);
                                }

                                //Track the buying of trade goods
                                //public void TrackData(string SystemName, string what, long value, string who, string extra = "")
                                TrackData(Globals.cursystem.ToUpper(), "Trade", Convert.ToInt64(line.Count), Globals.curstationfaction, (string)line.TotalCost, (string)line.Type);

                                break;
                            case "MarketSell":
                                //For the Current system: +count to traded.
                                DataRow SellSystem = dataSystems.Tables["StarSystems"].Rows.Find(Globals.cursystem.ToUpper());
                                if (SellSystem == null)
                                {
                                    DataRow newSystem = dataSystems.Tables["StarSystems"].NewRow();
                                    newSystem["SystemName"] = Globals.cursystem.ToUpper();
                                    if(line.StolenGoods == "true")
                                    {
                                        newSystem["Pawned"] = line.Count;
                                    }
                                    else
                                    {
                                        newSystem["Traded"] = line.Count;
                                    }
                                    
                                    dataSystems.Tables["StarSystems"].Rows.Add(newSystem);
                                }
                                else
                                {
                                    if (line.StolenGoods == "true")
                                    {
                                        SellSystem["Pawned"] = Convert.ToInt64(SellSystem["Pawned"]) + Convert.ToInt64(line.Count);
                                    }
                                    else
                                    {
                                        SellSystem["Traded"] = Convert.ToInt64(SellSystem["Traded"]) + Convert.ToInt64(line.Count);
                                    }
                                    
                                }

                                ////public void TrackData(string SystemName, string what, long value, string who, string extra = "")
                                if (line.StolenGoods == "true")
                                {
                                    //Track the selling of stolen goods (pawned)
                                    TrackData(Globals.cursystem.ToUpper(), "Pawned", Convert.ToInt64(line.Count), Globals.curstationfaction, (string)line.TotalSale, (string)line.Type);
                                }else
                                {
                                    //Track the selling of trade goods
                                    TrackData(Globals.cursystem.ToUpper(), "Trade", Convert.ToInt64(line.Count), Globals.curstationfaction, (string)line.TotalSale, (string)line.Type);
                                }

                                break;

                            //BH Turn-in
                            case "RedeemVoucher":
                                switch ((string)line.Type.ToUpper())
                                {

                                    case "BOUNTY":
                                        DataRow BHClaimSystem = dataSystems.Tables["StarSystems"].Rows.Find(Globals.cursystem.ToUpper());
                                        if (BHClaimSystem == null)
                                        {
                                            DataRow newSystem = dataSystems.Tables["StarSystems"].NewRow();
                                            newSystem["SystemName"] = Globals.cursystem.ToUpper();
                                            newSystem["BHClaims"] = Convert.ToInt64(line.Amount);
                                            dataSystems.Tables["StarSystems"].Rows.Add(newSystem);
                                        }
                                        else
                                        {
                                            BHClaimSystem["BHClaims"] = Convert.ToInt64(BHClaimSystem["BHClaims"]) + Convert.ToInt64(line.Amount);
                                        }



                                        if (line.Factions != null)
                                        {
                                            if (line.Factions.Count > 0)
                                            {
                                                foreach (dynamic factiondata in line.Factions)
                                                {
                                                   
                                                   TrackData(Globals.cursystem.ToUpper(), "BH", Convert.ToInt64(factiondata.Amount), (string)factiondata.Faction);
                                                }
                                            }else
                                            {
                                                TrackData(Globals.cursystem.ToUpper(), "BH", Convert.ToInt64(line.Amount), "");
                                            }
                                        }
                                        else
                                        {
                                            if (line.Faction != null)
                                            {
                                                TrackData(Globals.cursystem.ToUpper(), "BH", Convert.ToInt64(line.Amount), (string)line.Faction);
                                            }
                                            else
                                            {
                                                //No Factions :(
                                                TrackData(Globals.cursystem.ToUpper(), "BH", Convert.ToInt64(line.Amount), "");
                                            }
                                        }

                                        break;
                                    case "COMBATBOND":
                                        DataRow CZClaimSystem = dataSystems.Tables["StarSystems"].Rows.Find(Globals.cursystem.ToUpper());
                                        if (CZClaimSystem == null)
                                        {
                                            DataRow newSystem = dataSystems.Tables["StarSystems"].NewRow();
                                            newSystem["SystemName"] = Globals.cursystem.ToUpper();
                                            newSystem["CZClaims"] = Convert.ToInt64(line.Amount);
                                            dataSystems.Tables["StarSystems"].Rows.Add(newSystem);
                                        }
                                        else
                                        {
                                            CZClaimSystem["CZClaims"] = Convert.ToInt64(CZClaimSystem["CZClaims"]) + Convert.ToInt64(line.Amount);
                                        }

                                        //Track the redeeming of CZ.
                                        //CZ vouchers are for the faction who issued the voucher, this isn't in the journal atm.
                                        if (line.Factions != null)
                                        {
                                            if (line.Factions.Count > 0)
                                            {
                                                foreach (dynamic factiondata in line.Factions)
                                                {

                                                    TrackData(Globals.cursystem.ToUpper(), "CZ", Convert.ToInt64(factiondata.Amount), (string)factiondata.Faction);
                                                }
                                            }
                                            else
                                            {
                                                TrackData(Globals.cursystem.ToUpper(), "CZ", Convert.ToInt64(line.Amount), "");
                                            }
                                        }
                                        else
                                        {
                                            if (line.Faction != null)
                                            {
                                                TrackData(Globals.cursystem.ToUpper(), "CZ", Convert.ToInt64(line.Amount), (string)line.Faction);
                                            }
                                            else
                                            {
                                                //No Factions :(
                                                TrackData(Globals.cursystem.ToUpper(), "CZ", Convert.ToInt64(line.Amount), "");
                                            }
                                        }



                                        break;
                                    case "SETTLEMENT":
                                    case "SCANNABLE":
                                    case "TRADE":
                                        //We aren't adding these to the JTracker, simply throwing them into the tracker itself.
                                        TrackData(Globals.cursystem.ToUpper(), "Voucher", Convert.ToInt64(line.Amount), Globals.curstationfaction);
                                        break;
                                    default:
                                        TrackData(Globals.cursystem.ToUpper(), (string)line.Type.ToUpper(), Convert.ToInt64(line.Amount), Globals.curstationfaction);

                                        //Unknown claim type...
                                        //wb.Document.Write("Unknown Claim Type: " + (string)line.Type.ToUpper() + "<br>Let Cazz0r know!");
                                        break;

                                }
                                break;
                            case "CommitCrime":
                                //https://edcodex.info/?m=doc#f.10.3
                                //We've commited a crime.
                                bool crime = true;

                                //Not sure if we need to do things for different crimes, but all types are referenced: https://edcodex.info/?m=doc#f.11.6
                                switch ((string)line.CrimeType.ToUpper())
                                {
                                    case "ASSAULT":

                                        break;
                                    case "MURDER":
                                        //We've murdered somebody, this will likely result in a fine.

                                        break;
                                    case "PIRACY":

                                        break;
                                    case "INTERDICTION":

                                        break;
                                    case "ILLEGALCARGO":

                                        break;
                                    case "DISOBEYPOLICE":

                                        break;
                                    case "DUMPINGDANGEROUS":

                                        break;
                                    case "DUMPINGNEARSTATION":
                                        break;
                                    
                                    default: //Don't concern ourselves with any smaller crime.
                                        crime = false;
                                        break;
                                }

                                //For testing purposes we're turning on all crime tracking.
                                crime = true;

                                if (crime)
                                {
                                    DataRow CrimeSystem = dataSystems.Tables["StarSystems"].Rows.Find(Globals.cursystem.ToUpper());
                                    if (CrimeSystem == null)
                                    {
                                        DataRow crimeSystem = dataSystems.Tables["StarSystems"].NewRow();
                                        crimeSystem["SystemName"] = Globals.cursystem.ToUpper();
                                        crimeSystem["Fines"] = Convert.ToInt64(line.Fine) + Convert.ToInt64(line.Bounty);
                                        dataSystems.Tables["StarSystems"].Rows.Add(crimeSystem);
                                    }
                                    else
                                    {
                                        CrimeSystem["Fines"] = Convert.ToInt64(CrimeSystem["Fines"]) + Convert.ToInt64(line.Fine) + Convert.ToInt64(line.Bounty);
                                    }

                                    //Track the fine
                                    TrackData(Globals.cursystem.ToUpper(), "Fined", (Convert.ToInt64(line.Fine) + Convert.ToInt64(line.Bounty)), line.Faction);
                                }
                                break;
                            case "FactionKillBond": //These occurs when you destory a ship that issues a bond.
                                                    //Check if this was a bond we were wanting for a MASSACRE
                                                    //Current System matches "DestinationSystem"
                                                    //"AwardingFaction" matches "ForFaction" 
                                                    //"VictimFaction" matches "TargetFaction"
                                for (int i = frmMain.dataSystems.Tables["Overlay"].Rows.Count - 1; i >= 0; i--)
                                {
                                    var row = frmMain.dataSystems.Tables["Overlay"].Rows[i];
                                    if (
                                            (string)row["Type"] == "MASSACRE" &&
                                            (string)row["DestinationSystem"] == Globals.cursystem &&
                                            (string)row["ForFaction"] == (string)line.AwardingFaction &&
                                            (string)row["TargetFaction"] == (string)line.VictimFaction
                                        )
                                    {
                                        //This is exactly what we were after, we killed another + 1 to killed.
                                        row["TargetCountKilled"] = (int)row["TargetCountKilled"] + 1;
                                        if ((int)row["TargetCountKilled"] >= (int)row["TargetCount"])
                                        {
                                            row.Delete();
                                        }
                                    }
                                }
                                OverlayMissions();
                                TrackData(Globals.cursystem, "ShipKill2", 1, line.VictimFaction.ToString(), line.Reward.ToString());
                                break;
                            case "PVPKill":
                                TrackData(Globals.cursystem.ToUpper(), "PVP", (int)line.CombatRank, line.Victim);
                                break;
                            case "SetUserShipName":
                                Globals.curshipname = line.UserShipName;
                                Globals.curship = line.Ship;
                                if(line.UserShipId != null)
                                {
                                    if (line.UserShipId == "") line.UserShipId = "NO ID";
                                    Globals.curshipident = line.UserShipId;
                                }
                                UpdateHeaderLabels();
                                break;
                            default:
                                //Ignore.
                                break;
                        }
                    }
                    break;
            }


            
            return;
        }
        
