package com.pl.beerwars.data;
import android.content.*;
import android.widget.*;
import android.database.sqlite.*;
import android.database.*;
import com.pl.beerwars.data.playerdata.*;
import com.pl.beerwars.data.beer.*;
import java.util.*;

public class Storage extends SQLiteOpenHelper
{
	private static final int DB_VERSION = 1;
	private Context context;
	public Storage(Context context)
	{
		super(context, "beerwars_db", null, DB_VERSION);
		this.context = context;
	}

	@Override
	public void onCreate(SQLiteDatabase db)
	{
		Toast.makeText(context, "Creating database", Toast.LENGTH_SHORT).show();
		runScripts(db, 0, scripts.length - 1);
	}
	
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldver, int newver)
	{
		int firstS = 0;
		switch (oldver){
			case 0: firstS = 0; break;
			case 1: firstS = 9; break;
		}

		int lastS = 0;
		switch (newver){
			case 1: lastS = 8; break;
		}	
		//Toast.makeText(context, "Upgrading database", Toast.LENGTH_SHORT).show();
		runScripts(db, firstS, lastS);
	}

	private void runScripts(SQLiteDatabase db, int fromN, int toN){
		for (int i = fromN; i<=toN; i++){
			db.execSQL(scripts[i]);
		}
	}

	private String[] scripts = new String[]{
		//0
		"CREATE TABLE game (" +
		"id INTEGER PRIMARY KEY AUTOINCREMENT," +
		"savename TEXT," +
		"mapId INTEGER," +
		"playerCount INTEGER," +
		"date INTEGER)",
		//1
		"CREATE TABLE player (" +
		"id INTEGER PRIMARY KEY AUTOINCREMENT," +
		"gameId INTEGER," +
		"intellectId INTEGER," +
		"name TEXT," + 
		"playerNum INTEGER," +
		"money INTEGER)",
		//2
		"CREATE TABLE ownedSort (" +
		"playerId INTEGER," +
		"id INTEGER," +
		"name TEXT," + 
		"quality DECIMAL," +
		"selfprice DECIMAL)",
		//3
		"CREATE TABLE transportOrder (" +
		"playerId INTEGER," +
		"fromCity INTEGER," +
		"toCity INTEGER," + 
		"isRecurring INTEGER," +
		"quantity INTEGER)",
		//4
		"CREATE TABLE cityObject (" +
		"id INTEGER PRIMARY KEY AUTOINCREMENT," +
		"playerId INTEGER," +
		"cityId TEXT," +
		"storageSize INTEGER," + 
		"storageBuildRemaining INTEGER," +
		"factorySize INTEGER," +
		"factoryUnitsCount INTEGER," +
		"factoryBuildRemaining INTEGER)",
		//5
		"CREATE TABLE price (" +
		"cityObjectId INTEGER," +
		"beerId INTEGER," +
		"price DECIMAL)",
		//6
		"CREATE TABLE storageUnit (" +
		"cityObjectId INTEGER," +
		"beerId INTEGER," +
		"amount INTEGER)",
		//7
		"CREATE TABLE factoryUnit (" +
		"cityObjectId INTEGER," +
		"beerId INTEGER," +
		"working INTEGER)",
		//7
		"CREATE TABLE factoryUnitsExtension (" +
		"cityObjectId INTEGER," +
		"unitsCount INTEGER," +
		"weeksLeft INTEGER)"
	};
	
	public void save(Game game, String saveName){
		SQLiteDatabase db = this.getWritableDatabase();

		ContentValues values = new ContentValues();
		values.put("savename", saveName);
		values.put("date", game.date.getTime());
		values.put("mapId", game.map.mapId);
		long gameId = db.insert("game", null, values);
		
		for (int plN : game.players.keySet())
		{
			values = new ContentValues();
			values.put("playerNum", plN);
			values.put("gameId", gameId);
			PlayerData pl = game.players.get(plN);
			values.put("intellectId", pl.intellect_id);
			values.put("name", pl.name);
			values.put("money", pl.money);
			long playerId = db.insert("player", null, values);
			
			for (BeerSort sort : pl.ownedSorts){
				values = new ContentValues();
				values.put("playerId", playerId);
				values.put("id", sort.id);
				values.put("name", sort.name);
				values.put("quality", sort.quality);
				values.put("selfPrice", sort.selfprice);
				db.insert("ownedSort", null, values);
			}
			for (TransportOrder order : pl.oneTimeOrders){
				db.execSQL(String.format(
							   "INSERT into transportOrder (playerId, fromCity, toCity, isRecurring, quantity) " +
							   "values (%1$s, '%2$s', '%3$s', %4$s, %5$s)",
							   playerId, order.fromCity, order.toCity, 0, order.packageQuantity));
			}
			for (TransportOrder order : pl.recurringOrders){
				db.execSQL(String.format(
							   "INSERT into transportOrder (playerId, fromCity, toCity, isRecurring, quantity) " +
							   "values (%1$s, '%2$s', '%3$s', %4$s, %5$s)",
							   playerId, order.fromCity, order.toCity, 1, order.packageQuantity));
			}
			
			for (CityObjects co : pl.cityObjects){
				values = new ContentValues();
				values.put("playerId", playerId);
				values.put("cityId", co.cityRef.id);
				values.put("storageSize", ConvertStor(co.storageSize));
				values.put("storageBuildRemaining", co.storageBuildRemaining);
				values.put("factorySize", ConvertFact(co.factorySize));
				values.put("factoryUnitsCount", co.factoryUnits);
				values.put("factoryBuildRemaining", co.factoryBuildRemaining);
				long cobjId = db.insert("cityObject", null, values);				

				for (BeerSort sp : co.prices.keySet()){
					db.execSQL(String.format(
								   "INSERT into price (cityObjectId, beerId, price) " +
								   "values (%1$s, %2$s, %3$s)",
								   cobjId, sp.id, co.prices.get(sp)));
				}
				
				for (BeerSort ss : co.storage.keySet()){
					db.execSQL(String.format(
								   "INSERT into storageUnit (cityObjectId, beerId, amount) " +
								   "values (%1$s, %2$s, %3$s)",
								   cobjId, ss.id, co.storage.get(ss)));
					
				}
				
				for (BeerSort sf : co.factory.keySet()){
					db.execSQL(String.format(
								   "INSERT into factoryUnit (cityObjectId, beerId, working) " +
								   "values (%1$s, %2$s, %3$s)",
								   cobjId, sf.id, co.factory.get(sf)));
				}
				
				for (FactoryChange chg : co.factoryUnitsExtensions){
					db.execSQL(String.format(
								   "INSERT into factoryUnitsExtension (cityObjectId, unitsCount, weeksLeft) " +
								   "values (%1$s, %2$s, %3$s)",
								   cobjId, chg.unitsCount, chg.weeksLeft));
				}
			}
		}

		db.close();
	}
	
	public Game load(int id){
		Game game = new Game();
		
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.rawQuery("Select savename, mapId, date from game where id = " + id, null);
		if (cursor.moveToFirst()){
			int mapId = cursor.getInt(1);
			long date = cursor.getLong(2);
			game.map = GameHolder.getMap(mapId);
			game.date = new Date();
			game.date.setTime(date);
			game.players = new HashMap<Integer, PlayerData>();
			
			Cursor cursorPl = db.rawQuery("Select id, playerNum, intellectId, name, money from player where gameId = " + id, null);
			cursorPl.moveToFirst();
			do {
				int playerId = cursorPl.getInt(0);
				int plN = cursorPl.getInt(1);
				PlayerData p = new PlayerData(cursorPl.getInt(2), cursorPl.getString(3));
				p.id = plN;
				p.money = cursorPl.getInt(4);
				
				Cursor cursorInner = db.rawQuery("Select id, name, quality, selfprice from ownedSort where playerId = " + playerId, null);
				cursorInner.moveToFirst();
				do {
					BeerSort sort = new BeerSort(
						cursorInner.getInt(0), cursorInner.getString(1), cursorInner.getFloat(2), cursorInner.getFloat(3));
					p.ownedSorts.add(sort);
				} while (cursorInner.moveToNext());
				cursorInner.close();
				
				cursorInner = db.rawQuery("Select fromCity, toCity, quantity, isRecurring from transportOrder where playerId = " + playerId, null);
				if (cursorInner.moveToFirst()){
					do {
						TransportOrder tro = new TransportOrder();
						tro.fromCity = game.map.getCityById(cursorInner.getString(0));
						tro.toCity = game.map.getCityById(cursorInner.getString(1));
						tro.packageQuantity = cursorInner.getInt(2);
						boolean isRecurring = cursorInner.getInt(3) == 1;
						if (isRecurring)
							p.recurringOrders.add(tro);
						else
							p.oneTimeOrders.add(tro);
					} while (cursorInner.moveToNext());
				}
				cursorInner.close();								
				
				p.cityObjects = new CityObjects[game.map.cities.length];
				cursorInner = db.rawQuery("Select id, cityId, storageSize, storageBuildRemaining, factorySize, factoryUnitsCount, factoryBuildRemaining from cityObject where playerId = " + playerId, null);
				cursorInner.moveToFirst();
				do {
					int coid = cursorInner.getInt(0);
					CityObjects co = new CityObjects(
						game.map.getCityById(cursorInner.getString(1)),
						ToStor(cursorInner.getInt(2)),
						ToFact(cursorInner.getInt(4))
					);
					co.storageBuildRemaining = cursorInner.getInt(3);
					co.factoryUnits = cursorInner.getInt(5);
					co.factoryBuildRemaining = cursorInner.getInt(6);
					int index = game.map.getCityIndex(co.cityRef.id);
					p.cityObjects[index] = co;
					
					Cursor cSo = db.rawQuery("Select beerId, price from price where cityObjectId = " + coid, null);
					if (cSo.moveToFirst()){
						do{
							int sortId = cSo.getInt(0);
							float price = cSo.getFloat(1);
							co.prices.put(p.getSort(sortId), price);
						} while (cSo.moveToNext());
					}
					cSo.close();
					
					cSo = db.rawQuery("Select beerId, amount from storageUnit where cityObjectId = " + coid, null);
					if (cSo.moveToFirst()) {
						do{
							int sortId = cSo.getInt(0);
							int amount = cSo.getInt(1);
							co.storage.put(p.getSort(sortId), amount);
						} while (cSo.moveToNext());
					}
					cSo.close();

					cSo = db.rawQuery("Select beerId, working from factoryUnit where cityObjectId = " + coid, null);
					if (cSo.moveToFirst()) {
						do{
							int sortId = cSo.getInt(0);
							co.factory.put(p.getSort(sortId), cSo.getInt(1));
						} while (cSo.moveToNext());
					}
					cSo.close();
					

					cSo = db.rawQuery("Select unitsCount, weeksLeft from factoryUnitsExtension where cityObjectId = " + coid, null);
					if (cSo.moveToFirst()) {
						do{
							co.factoryUnitsExtensions.add(new FactoryChange(cSo.getInt(0), cSo.getInt(1)));
						} while (cSo.moveToNext());
					}
					cSo.close();
					
				} while (cursorInner.moveToNext());
				cursorInner.close();

				game.players.put(plN, p);
			} while (cursorPl.moveToNext());
			cursorPl.close();
		}
		cursor.close();

		game.start();
		
		return game;
	}
	
	public int getLatestId(){
		SQLiteDatabase db = this.getReadableDatabase();
		int id = -1;
		Cursor cursor = db.rawQuery("Select max(id) from game", null);
		if (cursor.moveToFirst()){
			id = cursor.getInt(0);
		}
		cursor.close();

		return id;
	}
	
	private int ConvertStor(Constants.StorageSize size){
		switch (size) {
			case Small: return 1;
			case Medium: return 2;
			case Big: return 3;
			case None:
			default: 
				return 0;
		}
	}

	private int ConvertFact(Constants.FactorySize size){
		switch (size) {
			case Small: return 1;
			case Medium: return 2;
			case Big: return 3;
			case None:
			default: 
				return 0;
		}
	}

	private Constants.StorageSize ToStor(int size){
		switch (size) {
			case 1: return Constants.StorageSize.Small;
			case 2: return Constants.StorageSize.Medium;
			case 3: return Constants.StorageSize.Small;
			case 0:
			default: 
				return Constants.StorageSize.None;
		}
	}

	private Constants.FactorySize ToFact(int size){
		switch (size) {
			case 1: return Constants.FactorySize.Small;
			case 2: return Constants.FactorySize.Medium;
			case 3: return Constants.FactorySize.Small;
			case 0:
			default: 
				return Constants.FactorySize.None;
		}
	}
}

/*
package com.pl.slalom.data.database;
import com.pl.slalom.*;
import com.pl.slalom.data.*;
import com.pl.slalom.data.race.*;
import android.database.sqlite.*;
import android.content.*;
import android.database.*;
import android.widget.*;
import java.util.*;
import com.pl.slalom.data.race.*;
import com.pl.slalom.data.achievment.*;

public class SqlLiteDataLoader extends SQLiteOpenHelper implements IDataLoader
{
	private static final int DB_VERSION = 10;
	private Context context;
	public SqlLiteDataLoader(Context context)
	{
		super(context, "slalom_db", null, DB_VERSION);
		this.context = context;
	}

	@Override
	public void onCreate(SQLiteDatabase db)
	{
		Toast.makeText(context, "Creating database", Toast.LENGTH_SHORT).show();
		runScripts(db, 0, scripts.length - 1);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldver, int newver)
	{
		int firstS = 0;
		switch (oldver){
			case 1: firstS = 6; break;
			case 2: firstS = 10; break;
			case 3: firstS = 11; break;
			case 4: firstS = 13; break;
			case 5: firstS = 14; break;
			case 6: firstS = 15; break;
			case 7: firstS = 19; break;
			case 8: firstS = 24; break;
			case 9: firstS = 27; break;
			case 10: firstS = 29; break;
		}
		
		int lastS = 0;
		switch (newver){
			case 1: lastS = 5; break;
			case 2: lastS = 9; break;
			case 3: lastS = 10; break;
			case 4: lastS = 12; break;
			case 5: lastS = 13; break;
			case 6: lastS = 14; break;
			case 7: lastS = 18; break;
			case 8: lastS = 23; break;
			case 9: lastS = 26; break;
			case 10: lastS = 28; break;
		}	
		Toast.makeText(context, "Upgrading database", Toast.LENGTH_SHORT).show();
		runScripts(db, firstS, lastS);
	}
	
	private void runScripts(SQLiteDatabase db, int fromN, int toN){
		for (int i = fromN; i<=toN; i++){
			db.execSQL(scripts[i]);
		}
	}

	private String[] scripts = new String[]{
		//0
		"CREATE TABLE player (" +
		"id INTEGER PRIMARY KEY AUTOINCREMENT," +
		"name TEXT," +
		"money INTEGER," +
		"experience INTEGER)",
		//1
		"INSERT INTO player(name, money, experience) values (null, 0, 0)",
		//2
		"CREATE TABLE ski_availability (" +
		"id INTEGER PRIMARY KEY AUTOINCREMENT," +
		"skiId INTEGER," +
		"playerId Integer)",
		//3
		"INSERT INTO ski_availability(skiId, playerId)" +
		" select 10, p.id from player p",
		//4
		"CREATE TABLE track_availability (" +
		"id INTEGER PRIMARY KEY AUTOINCREMENT," +
		"trackId INTEGER," +
		"playerId Integer)",
		//5
		"INSERT INTO track_availability(trackId, playerId)" +
		" select 3, p.id from player p",
		//v2
		//6
		"CREATE TABLE competition (" +
		"id INTEGER PRIMARY KEY AUTOINCREMENT," +
		"playerId INTEGER)",
		//7
		"CREATE TABLE race (" +
		"id INTEGER PRIMARY KEY AUTOINCREMENT," +
		"competitionId INTEGER," +
		"trackId INTEGER," +
		"orderNum INTEGER," +
		"runCount INTEGER)",
		//8
		"CREATE TABLE competitor (" +
		"id INTEGER PRIMARY KEY AUTOINCREMENT," +
		"competitionId INTEGER," +
		"name TEXT," +
		"countryId INTEGER," +
		"skiId INTEGER," +
		"aiId INTEGER," +
		"points INTEGER)",
		//9
		"CREATE TABLE run (" +
		"id INTEGER PRIMARY KEY AUTOINCREMENT," +
		"competitorId INTEGER," +
		"raceId INTEGER," +
		"runNumber INTEGER," +
		"turns INTEGER," +
		"time DECIMAL," +
		"status INTEGER)",
		//v3
		//10
		"ALTER TABLE competition ADD COLUMN currentRace INTEGER",
		//v4
		//11
		"DELETE FROM track_availability where trackId = 3",
		//12
		"INSERT INTO track_availability(trackId, playerId)" +
		" select 10, p.id from player p",
		//v5
		//13
		"ALTER TABLE player ADD COLUMN lastName TEXT",
		//14
		"ALTER TABLE player ADD COLUMN countryId INTEGER",
		//v7
		//15
		"ALTER TABLE player ADD COLUMN selectedSkiId INTEGER",
		//16
		"UPDATE player SET selectedSkiId = 10",
		//17
		"ALTER TABLE competition ADD COLUMN type INTEGER",
		//18
		"UPDATE competition SET type = " + Constants.CompetitionType.MULTIPLAYER,
		//v8
		//19
		"CREATE TABLE achievement (" +
		"id INTEGER PRIMARY KEY AUTOINCREMENT," +
		"playerId INTEGER," +
		"achievementIdend INTEGER," +
		"type INTEGER)",
		//20
		"CREATE TABLE resultRace (" +
		"achievementid," +
		"turns INTEGER," +
		"time DECIMAL," +
		"place INTEGER)",
		//21
		"ALTER TABLE race ADD COLUMN resultMeasureType INTEGER",
		//22
		"UPDATE race SET resultMeasureType = " + convert(ResultMeasureType.TurnsAndTime),
		//23
		"ALTER TABLE competition ADD COLUMN competitionDefId INTEGER",
		//v9
		//24
		"DROP TABLE resultRace",
		//25
		"CREATE TABLE resultSlope (" +
		"achievementid," +
		"turns INTEGER," +
		"time DECIMAL)",
		//26
		"CREATE TABLE resultCompetition (" +
		"achievementid," +
		"place INTEGER)",
		//v10
		//27
		"DROP TABLE achievement",
		//28
		"CREATE TABLE achievement (" +
		"id INTEGER PRIMARY KEY AUTOINCREMENT," +
		"playerId INTEGER," +
		"achievementIdent INTEGER," +
		"type INTEGER)"
	};

	@Override
	public Data loadData()
	{
		Toast.makeText(context, "Loading data", Toast.LENGTH_SHORT).show();
		
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.rawQuery("Select id, name, lastName, countryId, money, experience, selectedSkiId from player", null);
		if (cursor.moveToFirst()){
			Data data = new Data();
			data.id = cursor.getInt(0);
			data.name = cursor.getString(1);
			data.lastname = cursor.getString(2);
			data.countryId = cursor.getInt(3);
			data.money = cursor.getInt(4);
			data.experience = cursor.getInt(5);
			data.selectedSkiId = cursor.getInt(6);
			cursor.close();
			data.availableSkiIds = getAvailableSkis(db, data.id);
			data.availableTrackIds = getAvailableSlopes(db, data.id);
			return data;
		}
		cursor.close();
		
		return null;
	}
	
	private List<Integer> getAvailableSkis(SQLiteDatabase db, int playerId){
		List<Integer> result = new LinkedList<Integer>();
		
		Cursor cursor = db.rawQuery("Select skiId from ski_availability where playerId = " + playerId, null);
		if (cursor.moveToFirst()){
			do{
				result.add(cursor.getInt(0));
			} while (cursor.moveToNext());
		}
		cursor.close();

		return result;
	}

	private List<Integer> getAvailableSlopes(SQLiteDatabase db, int playerId){
		List<Integer> result = new LinkedList<Integer>();

		Cursor cursor = db.rawQuery("Select trackId from track_availability where playerId = " + playerId, null);
		if (cursor.moveToFirst()){
			do{
				result.add(cursor.getInt(0));
			} while (cursor.moveToNext());
		}
		cursor.close();

		return result;
	}
	
	@Override
	public void storeData(Data data)
	{
		SQLiteDatabase db = this.getWritableDatabase();
		
		ContentValues values = new ContentValues();
		values.put("name", data.name);
		values.put("lastname", data.lastname);
		values.put("countryId", data.countryId);
		values.put("money", data.money);
		values.put("experience", data.experience);
		values.put("selectedSkiId", data.selectedSkiId);
		db.update("player", values, "id = ?", 
			new String[] {String.valueOf(data.id)});
		
		storeAvailableSkis(db, data);
		storeAvailableSlopes(db, data);
		
		db.close();
	}
	
	private void storeAvailableSkis(SQLiteDatabase db, Data data){
		
	}
	
	private void storeAvailableSlopes(SQLiteDatabase db, Data data){
		List<Integer> current = new LinkedList<Integer>();

		for (Integer slopeId : data.availableTrackIds){
			if (!current.contains(slopeId)) {
				ContentValues v = new ContentValues();
				v.put("playerId", data.id);
				v.put("trackId", slopeId);
				db.insert("track_availability", null, v);
			}
		}
		
		for (Integer slopeId : current){
			if (!data.availableTrackIds.contains(slopeId)) {
				ContentValues v = new ContentValues();
				v.put("playerId", data.id);
				v.put("trackId", slopeId);
				db.execSQL("DELETE FROM track_availability WHERE playerId = " +
					data.id + " AND trackId = " + slopeId);
			}
		}
	}
	
	public void deleteCompetition(Competition competition){
		SQLiteDatabase db = this.getWritableDatabase();
		
		db.execSQL("delete from run where competitorId in (select id from competitor where competitionId = " + competition.id + ")");
		db.execSQL("delete from race where competitionId = " + competition.id);
		db.execSQL("delete from competitor where competitionId = " + competition.id);
		db.execSQL("delete from competition where id = " + competition.id);
		
		db.close();
	}

	public void insertCompetition(long playerId, Competition competition, int competitionType){
		SQLiteDatabase db = this.getWritableDatabase();
		
		ContentValues v = new ContentValues();
		v.put("playerId", playerId);
		v.put("currentRace", competition.currentRace);
		v.put("type", competitionType);
		v.put("competitionDefId", competition.definition == null ? -1 : competition.definition.id);
		competition.id = db.insert("competition", null, v);
		
		for (int i = 0; i < competition.competitors.size(); i++){
			Competitor c = competition.competitors.get(i);
			v = new ContentValues();
			v.put("competitionId", competition.id);
			v.put("name", c.name);
			v.put("countryId", c.countryId);
			v.put("skiId", c.skiId);
			v.put("aiId", c.ai_id);
			v.put("points", c.points);
			c.id = db.insert("competitor", null, v);
		}
		
		for (int i = 0; i < competition.races.size(); i++){
			Race r = competition.races.get(i);
			v = new ContentValues();
			v.put("competitionId", competition.id);
			v.put("orderNum", r.order);
			v.put("trackId", r.trackId);
			v.put("runCount", r.runCount);
			r.id = db.insert("race", null, v);
		

			for (int ic = 0; ic < competition.competitors.size(); ic++){
				for (int rc = 0; rc < r.runCount; rc++){
					v = new ContentValues();
					v.put("competitorId", competition.competitors.get(ic).id);
					v.put("raceId", r.id);
					v.put("runNumber", rc);
					db.insert("run", null, v);
					// other parameters of 'run' are stored by 'updateCompetition' and should be called separately
				}
			}
		}
		
		db.close();
	}
	
	public void updateCompetition(Competition competition){
		SQLiteDatabase db = this.getWritableDatabase();
		
		ContentValues v;

		for (int ir = 0; ir < competition.races.size(); ir++){
			Race r = competition.races.get(ir);
			for (int ic = 0; ic < competition.competitors.size(); ic++){
				for (int irc = 0; irc < r.runCount; irc++){
					RaceRun run = r.playerRuns[ic][irc];
					v = new ContentValues();
					v.put("status", getIntStatus(run.runResult.status));
					v.put("turns", run.runResult.turns);
					v.put("time", run.runResult.time);
					db.update("run", v, "id = ?",
						new String[] {String.valueOf(run.id)});
				}
			}
		}

		db.close();		
	}
	
	private int getIntStatus(RunStatus status){
		switch (status){
			case NotStarted: return 0;
			case Finished: return 10;
			case Failed: return 20;
			default:
				return -1;
		}
	}

	private RunStatus getStatus(int code){
		switch (code){
			case 0: return RunStatus.NotStarted;
			case 10: return RunStatus.Finished;
			case 20: return RunStatus.Failed;
			default:
				return RunStatus.NotStarted;
		}
	}
	
	public 	Competition getCompetitionByType(long playerId, int competitionType){
		
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.rawQuery(
			"Select id from competition where playerId = " 
			+ playerId + " and type = " + competitionType 
			, null);
		if (!cursor.moveToFirst())
			return null;
			
		int id = cursor.getInt(0);

		cursor.close();
		db.close();
		
		return getCompetitionById(id);
	}
	
	public Competition getCompetitionById(long competitionId){
		
		SQLiteDatabase db = this.getReadableDatabase();
		
		Cursor cursor = db.rawQuery(
				"Select id, currentRace, competitionDefId from competition where id = " 
				+ competitionId, null);
		if (!cursor.moveToFirst())
			return null;
		
		int defId = cursor.getInt(2);
		
		Competition c = new Competition(defId > 0 
			? new CompetitionManager().getCompetitionDef(context, defId)
			: null);
		c.id = cursor.getInt(0);
		c.currentRace = cursor.getInt(1);
		cursor.close();
		
		c.competitors = new LinkedList<Competitor>();
		cursor = db.rawQuery("select id, name, skiId, countryId, aiId, points from competitor where competitionId = " + c.id, null);
		if (cursor.moveToFirst()){
			do{
				long id = cursor.getInt(0);
				Competitor pl = new Competitor(
					cursor.getString(1),
					cursor.getInt(2),
					cursor.getInt(3),
					cursor.getInt(4),
					cursor.getInt(5));
				pl.id = id;
				c.competitors.add(pl);
			} while (cursor.moveToNext());
		}
		cursor.close();

		c.races = new LinkedList<Race>();
		cursor = db.rawQuery("select id, trackId, orderNum, runCount, resultMeasureType from race where competitionId = " + c.id, null);
		if (cursor.moveToFirst()){
			do{
				long id = cursor.getInt(0);
				int runc = cursor.getInt(3);
				Race r = new Race(
					cursor.getInt(1),
					cursor.getInt(2),
					runc,
					convert(cursor.getInt(3)),
					new RaceRun[c.competitors.size()][runc]);
				r.id = id;
				c.races.add(r);
			} while (cursor.moveToNext());
		}
		cursor.close();
		

		///
		for (int ir = 0; ir < c.races.size(); ir++){
			Race r = c.races.get(ir);
		 	cursor = db.rawQuery("select id, competitorId, runNumber, turns, time, status from run where raceId = " + r.id, null);
			
			cursor.moveToFirst();
			do{
				int id = cursor.getInt(0);
				int compId = cursor.getInt(1);
				int runN = cursor.getInt(2);
				
				int plN = 0;
				while (c.competitors.get(plN).id != compId) plN++;
				
				RaceRun rr = new RaceRun(getStatus(cursor.getInt(5)));
				rr.runResult.turns = cursor.getInt(3);
				float time = cursor.getFloat(4);
				rr.runResult.time = Math.round(time * 1000) / 1000f;
				rr.id = id;
				r.playerRuns[plN][runN] = rr;
			} while (cursor.moveToNext());
			cursor.close();
		}
		
		db.close();
		return c;
	}
	
	public int convert(ResultMeasureType resType){
		switch (resType){
			case Time: return 1;
			case Turns: return 2;
			case TurnsAndTime: return 3;
			default: return -1;
		}
	}

	public ResultMeasureType convert(int resType){
		switch (resType){
			case 1: return ResultMeasureType.Time;
			case 2: return ResultMeasureType.Turns;
			case 3: return ResultMeasureType.TurnsAndTime;
			default: return null;
		}
	}
	
	public List<Achievement> getAllAchievements(long playerId){
		List<Achievement> result = new LinkedList<Achievement>();
		SQLiteDatabase db = this.getReadableDatabase();

		Cursor cursor = db.rawQuery(
			"Select a.id, a.achievementIdent, a.type, " +
			"rs.turns, rs.time, " +
			"rc.place " +
			"from achievement a " + 
			"left outer join resultSlope rs on rs.achievementid = a.id " +
			"left outer join resultCompetition rc on rc.achievementid = a.id " +
			"where a.playerId = " + playerId, null);
		if (!cursor.moveToFirst())
			return result;
		do {
			Achievement a = new Achievement();
			a.id = cursor.getLong(0);
			a.identifier = cursor.getInt(1);
			int type = cursor.getInt(2);
			switch (type)
			{
				case SlopeResult.TYPE:
					SlopeResult r = new SlopeResult(
						cursor.getInt(3),
						cursor.getFloat(4)
					);
					a.result = r;
					break;
				case CompetitionResult.TYPE:
					CompetitionResult cr = new CompetitionResult(
						cursor.getInt(5)
					);
					a.result = cr;
					break;
					
			} 
			result.add(a);
		} while (cursor.moveToNext());
		
		cursor.close();
		db.close();
		return result;
	}
	
	public void storeAchievement(long playerId, Achievement a){
		SQLiteDatabase db = this.getReadableDatabase();
	
		db.execSQL("DELETE FROM resultSlope WHERE achievementid = " + a.id);
		db.execSQL("DELETE FROM resultCompetition WHERE achievementid = " + a.id);			

		if (a.id == 0){
			db.execSQL("DELETE FROM achievement WHERE id = " + a.id);
			
			ContentValues v = new ContentValues();
			v.put("playerId", playerId);
			v.put("achievementIdent", a.identifier);
			v.put("type", a.result.getType());
			a.id = db.insert("achievement", null, v);
			
			insertUpdateResult(a, db, true);
		} else{
			insertUpdateResult(a, db, true);
		}

		db.close();
	}
	
	private void insertUpdateResult(Achievement a, SQLiteDatabase db, boolean insert){
		ContentValues v = new ContentValues();
		String tableName = "none";
		if (a.result.getType() == SlopeResult.TYPE){
			SlopeResult rr = (SlopeResult)a.result;
			v.put("achievementid", a.id);
			v.put("turns", rr.turns);
			v.put("time", rr.time);
			tableName = "resultSlope";
		}
		if (a.result.getType() == CompetitionResult.TYPE){
			v.put("achievementid", a.id);
			v.put("place", ((CompetitionResult)a.result).place);
			tableName = "resultCompetition";			
		}

		if (insert)		
			db.insert(tableName, null, v);
		else
			db.update(tableName, v, "achievementid = ",
					  	new String[] {String.valueOf(a.id)});
	}

	public void ExecuteNonQuery(String sql){
		SQLiteDatabase db = getWritableDatabase();
		
		db.execSQL(sql);
		
		db.close();
	}
	
	public Cursor ExecuteQuery(String sql){
		SQLiteDatabase db = getWritableDatabase();

		return db.rawQuery(sql, null);
	}
}
*/
