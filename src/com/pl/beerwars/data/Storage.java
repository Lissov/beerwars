package com.pl.beerwars.data;
import android.content.*;
import android.widget.*;
import android.database.sqlite.*;
import android.database.*;
import com.pl.beerwars.data.playerdata.*;
import com.pl.beerwars.data.beer.*;
import java.util.*;
import com.pl.beerwars.data.map.*;
import com.pl.beerwars.ArtIntelligence.*;

public class Storage extends SQLiteOpenHelper
{
	private static final int DB_VERSION = 1;
	private Context context;
	public Storage(Context context)
	{
		super(context, "beerwars", null, DB_VERSION);
		this.context = context;
	}

	@Override
	public void onCreate(SQLiteDatabase db)
	{
		Toast.makeText(context, "Creating database", Toast.LENGTH_SHORT).show();
		runScripts(db, 0, getVerLastIndex(DB_VERSION));
	}

	private int getVerLastIndex(int version){
		switch (version){
			case 0: return -1;
			case 1: return 9;
			default:
				return -1;
		}
	}
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldver, int newver)
	{
		int firstS = getVerLastIndex(oldver) + 1;

		int lastS = getVerLastIndex(newver);
		
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
		"date INTEGER,"+
		"turnnum INTEGER,"+
		"complete INTEGER)",
		//1
		"CREATE TABLE player (" +
		"id INTEGER PRIMARY KEY AUTOINCREMENT," +
		"gameId INTEGER," +
		"intellectId INTEGER," +
		"name TEXT," + 
		"playerNum INTEGER," +
		"money INTEGER," +
		"ai_state TEXT)",
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
		"fromCity TEXT," +
		"toCity TEXT," + 
		"sortId INTEGER," +
		"quantity INTEGER," +
		"isRecurring INTEGER)",
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
		//8
		"CREATE TABLE factoryUnitsExtension (" +
		"cityObjectId INTEGER," +
		"unitsCount INTEGER," +
		"weeksLeft INTEGER)",
		//9
		"CREATE TABLE marketHistory (" +
		"playerId INTEGER," +
		"turnNumber INTEGER," +
		"cityId TEXT," +
		"beerId INTEGER," +
		"amount INTEGER)",
	};
	
	public void save(Game game, String saveName){
		SQLiteDatabase db = this.getWritableDatabase();
		
		db.beginTransaction();
		
		ContentValues values = new ContentValues();
		values.put("savename", saveName);
		values.put("date", game.date.getTime());
		values.put("turnnum", game.turnNum);
		values.put("mapId", game.map.mapId);
		values.put("complete", 0);
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
			values.put("ai_state", 
					pl.artIntelligence == null 
						? "" 
						: pl.artIntelligence.getState());
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
							   "INSERT into transportOrder (playerId, fromCity, toCity, sortId, quantity, isRecurring) " +
							   "values (%1$s, '%2$s', '%3$s', %4$s, %5$s, %6$s)",
							   playerId, order.fromCity, order.toCity, order.sort.id, order.packageQuantity, 0));
			}
			for (TransportOrder order : pl.recurringOrders){
				db.execSQL(String.format(
							   "INSERT into transportOrder (playerId, fromCity, toCity, sortId, quantity, isRecurring) " +
							   "values (%1$s, '%2$s', '%3$s', %4$s, %5$s, %6$s)",
							   playerId, order.fromCity, order.toCity, order.sort.id, order.packageQuantity, 1));
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
				
				for (Integer turn : co.consumptionHistory.keySet()){
					HashMap<BeerSort, Integer> turnsold = co.consumptionHistory.get(turn);

					for (BeerSort sort : turnsold.keySet()){
						db.execSQL(String.format(
							"INSERT into marketHistory (turnNumber, playerId, cityId, beerId, amount) " +
							"values (%1$s, %2$s, '%3$s', %4$s, %5$s)",
							turn, playerId, co.cityRef.id, sort.id, turnsold.get(sort)));
					}
				}
			}
		}

		db.execSQL("update game set complete = 1 where id = " + gameId);
		db.setTransactionSuccessful();
		db.endTransaction();

		db.close();

		//Toast.makeText(context, "Save complete: " + saveName, Toast.LENGTH_SHORT).show();
	}
	
	public Game load(int id){
		Game game = new Game();
		
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.rawQuery("Select savename, mapId, date, turnnum from game where id = " + id, null);
		if (cursor.moveToFirst()){
			int mapId = cursor.getInt(1);
			long date = cursor.getLong(2);
			game.map = GameHolder.getMap(mapId);
			game.date = new Date();
			game.date.setTime(date);
			game.turnNum = cursor.getInt(3);
			game.players = new HashMap<Integer, PlayerData>();
			
			Cursor cursorPl = db.rawQuery("Select id, playerNum, intellectId, name, money, ai_state from player where gameId = " + id, null);
			cursorPl.moveToFirst();
			do {
				int playerId = cursorPl.getInt(0);
				int plN = cursorPl.getInt(1);
				PlayerData p = new PlayerData(cursorPl.getInt(2), cursorPl.getString(3));
				p.id = plN;
				p.money = cursorPl.getInt(4);
				p.artIntelligence = AIHolder.getArtIntelligence(p.intellect_id);
				if (p.artIntelligence != null){
					p.artIntelligence.setState(cursorPl.getString(5));
				}
				
				Cursor cursorInner = db.rawQuery("Select id, name, quality, selfprice from ownedSort where playerId = " + playerId, null);
				cursorInner.moveToFirst();
				do {
					BeerSort sort = new BeerSort(
						cursorInner.getInt(0), cursorInner.getString(1), cursorInner.getFloat(2), cursorInner.getFloat(3));
					p.ownedSorts.add(sort);
				} while (cursorInner.moveToNext());
				cursorInner.close();
				
				cursorInner = db.rawQuery("Select fromCity, toCity, sortId, quantity, isRecurring from transportOrder where playerId = " + playerId, null);
				if (cursorInner.moveToFirst()){
					do {
						TransportOrder tro = new TransportOrder();
						tro.fromCity = game.map.getCityById(cursorInner.getString(0));
						tro.toCity = game.map.getCityById(cursorInner.getString(1));
						tro.sort = p.getSort(cursorInner.getInt(2));
						tro.packageQuantity = cursorInner.getInt(3);
						boolean isRecurring = cursorInner.getInt(4) == 1;
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
					
					readMarketHistory(db, playerId, co, p);
					
				} while (cursorInner.moveToNext());
				cursorInner.close();

				game.players.put(plN, p);
			} while (cursorPl.moveToNext());
			cursorPl.close();
		}
		cursor.close();

		game.start();
		
		//db.close();
		
		return game;
	}
	
	private void readMarketHistory(SQLiteDatabase db, int playerId, CityObjects co, PlayerData player){
			
		if (co.consumptionHistory == null)
			co.consumptionHistory = new HashMap<Integer, HashMap<BeerSort, Integer>>();
			
		Cursor curs = db.rawQuery(
				"SELECT turnNumber, beerId, amount " +
				"FROM marketHistory where cityId = '" + co.cityRef.id + 
				"' AND playerId = " + playerId, null);
				
		if (curs.moveToFirst()) {
			do{
				int turnnum = curs.getInt(0);
				
				if (!co.consumptionHistory.containsKey(turnnum))
					co.consumptionHistory.put(turnnum, new HashMap<BeerSort, Integer>());
				HashMap<BeerSort, Integer> cons = co.consumptionHistory.get(turnnum);
					
				BeerSort beer = player.getSort(curs.getInt(1));
				cons.put(beer, curs.getInt(2));
			} while (curs.moveToNext());
		}
		curs.close();
	}
	
	public int getLatestId(){
		SQLiteDatabase db = this.getReadableDatabase();
		int id = -1;
		Cursor cursor = db.rawQuery("Select max(id) from game", null);
		if (cursor.moveToFirst() && !cursor.isNull(0)){
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
	
	public List<SaveData> getSaves(){
		
		List<SaveData> res = new LinkedList<SaveData>();
		SQLiteDatabase db = this.getReadableDatabase();
		
		Cursor c = db.query(false, "game", new String[] {"id", "savename", "date", "mapId", "playerCount", "complete"}, 
			"", new String[0], "", "", "", "");
		
		if (c.moveToFirst()){
			do {
				SaveData sd = new SaveData();
				sd.id = c.getInt(0);
				sd.name = c.getString(1);
				sd.date = new Date();
				sd.date.setTime(c.getLong(2));
				sd.mapId = c.getInt(3);
				sd.consistent = c.getInt(5) == 1;
				res.add(sd);
			} while (c.moveToNext());
		}
		
		c.close();
		
		//db.close();
		
		return res;
	}

	public void ExecuteNonQuery(String sql){
		SQLiteDatabase db = getWritableDatabase();

		db.execSQL(sql);

		db.close();
	}

	public Cursor ExecuteQuery(String sql){
		
		SQLiteDatabase db= getReadableDatabase();
		try{
			return db.rawQuery(sql, null);
		} finally {
			//db.close();
		}
	}
}
