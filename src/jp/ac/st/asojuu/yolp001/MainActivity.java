package jp.ac.st.asojuu.yolp001;

import jp.co.yahoo.android.maps.GeoPoint;
import jp.co.yahoo.android.maps.MapController;
import jp.co.yahoo.android.maps.MapView;
import jp.co.yahoo.android.maps.navi.NaviController;
import jp.co.yahoo.android.maps.navi.NaviController.NaviControllerListener;
import jp.co.yahoo.android.maps.routing.RouteOverlay;
import jp.co.yahoo.android.maps.routing.RouteOverlay.RouteOverlayListener;
import jp.co.yahoo.android.maps.weather.WeatherOverlay;
import jp.co.yahoo.android.maps.weather.WeatherOverlay.WeatherOverlayListener;
import android.app.Activity;
import android.content.Context;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.Menu;

public class MainActivity extends Activity implements LocationListener, WeatherOverlayListener, NaviControllerListener,RouteOverlayListener{


	//LocationManagrを準備
	LocationManager mLocationManager = null;
	//MapViewを準備
	MapView mMapView = null;
	//直前の緯度(1000000倍精度）
	int lastLatitude = 0;
	//直前の経度（1000000倍精度）
	int lastLongitude = 0;

	//雨雲レーダー表示用のオーバレイクラス変数を準備
	WeatherOverlay mWeatherOverlay = null;

	//ルーティング
	NaviController mNaviController = null;
	RouteOverlay mRouteOverlay = null;


	@Override
	public void errorUpdateWeather(WeatherOverlay arg0, int arg1) {
		// TODO 自動生成されたメソッド・スタブ

	}

	@Override
	public void finishUpdateWeather(WeatherOverlay arg0) {
		// TODO 自動生成されたメソッド・スタブ

	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public void onLocationChanged(Location location) {
		// TODO 自動生成されたメソッド・スタブ

		// 緯度の取得
		double lat = location.getLatitude();
		int latitude = (int)(lat * 1000000);
		// 経度の取得
		double lon = location.getLongitude();
		int longitude = (int)(lon * 1000000);

		//緯度と経度のいずれかが直前の値と誤差が出れば、画面を更新（100で割ってもともとの緯度経度少数4ケタ、100ｍ位の誤差にする）
		if(latitude/1000 != this.lastLatitude/1000 || longitude/1000 != this.lastLongitude/1000){
			// 緯度経度情報の生成
			GeoPoint gp = new GeoPoint(latitude, longitude);
			// 地図本体を取得
			MapController c = mMapView.getMapController();
			// 地図本体にGeoPointを設定
			c.setCenter(gp);

			//今回の緯度経度を覚える
			this.lastLatitude = latitude;
			this.lastLongitude = longitude;
		}

	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
		// TODO 自動生成されたメソッド・スタブ

	}

	@Override
	public void onProviderEnabled(String provider) {
		// TODO 自動生成されたメソッド・スタブ

	}

	@Override
	protected void onResume() {
		// TODO 自動生成されたメソッド・スタブ
		super.onResume();

		// 地図表示用のYahooライブラリview部品を用意
		mMapView = new MapView(this,"dj0zaiZpPTdhZ1hERlB4QU01ViZzPWNvbnN1bWVyc2VjcmV0Jng9Mjg-");
		// ズームボタンを画面にON
		mMapView.setBuiltInZoomControls(true);
		// 地図縮尺バーを画面にON
		mMapView.setScalebar(true);

		// ここから、手動で地図をセット
		// 渋谷駅の座標をGeoPointを手書きで設定
		double lat = 35.658516;
		double lon = 139.701773;
		GeoPoint gp = new GeoPoint((int)(lat * 1000000),(int)(lon * 1000000));
		// 地図本体を取得
		MapController c = mMapView.getMapController();

		// 地図本体にGeoPointを設定
		c.setCenter(gp);
		// 地図本体のズームを３に設定
		c.setZoom(3);
		// 地図本体を画面にセット
		setContentView(mMapView);

		// ここから、GPSの使用
		// LocationManagerを取得
		mLocationManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);

		// 位置測定のためのGPS制度や使用っ消費電力を設定するふるいにかけるためのCriteriaオブジェクトを生成
		Criteria criteria = new Criteria();

		//Accuracyを指定（低精度）
		criteria.setAccuracy(Criteria.ACCURACY_COARSE);

		//PowerRequirementを指定（低消費電力）
		criteria.setPowerRequirement(Criteria.POWER_LOW);

		// 位置情報を伝達してくれるロケーションプロバイダの取得
		String provider = mLocationManager.getBestProvider(criteria,true);

		// 位置情報のイベントリスナーであるLocationListenerを登録
		mLocationManager.requestLocationUpdates(provider, 0, 0, this);

		// ここから雨雲レーダー処理
		// 雨雲レーダー用のオーバーレイ設定処理
		mWeatherOverlay = new WeatherOverlay(this);

		//WeatherOverlayListenerを設定
		mWeatherOverlay.setWeatherOverlayListener(this);

		//雨雲レーダーの更新間隔を、分単位で指定
		mWeatherOverlay.startAutoUpdate(1);

		//MapViewにWeatherOverlayを追加
		mMapView.getOverlays().add(mWeatherOverlay);

		//ここからルート検索
		mRouteOverlay = new RouteOverlay(this,"dj0zaiZpPTdhZ1hERlB4QU01ViZzPWNvbnN1bWVyc2VjcmV0Jng9Mjg-");

		//

	}

	@Override
	public void onProviderDisabled(String provider) {
		// TODO 自動生成されたメソッド・スタブ

	}

	@Override
	public boolean errorRouteSearch(RouteOverlay arg0, int arg1) {
		// TODO 自動生成されたメソッド・スタブ
		return false;
	}

	@Override
	public boolean finishRouteSearch(RouteOverlay arg0) {
		// TODO 自動生成されたメソッド・スタブ

		//NaviControllerを作成しRouteOverlayインスタンスを設定
		mNaviController = new NaviController(this,mRouteOverlay);

		//MapViewインスタンスを設定
		mNaviController.setMapView(mMapView);

		//NaviControllerListenerを設定
		mNaviController.setNaviControlListener(this);

		//案内処理を開始
		mNaviController.start();
		return false;
	}

	@Override
	public boolean onGoal(NaviController arg0) {
		// TODO 自動生成されたメソッド・スタブ
		//案内処理を継続しない場合は停止させる
		mNaviController.stop();
		return false;
	}

	@Override
	public boolean onLocationAccuracyBad(NaviController arg0) {
		// TODO 自動生成されたメソッド・スタブ
		return false;
	}

	@Override
	public boolean onLocationChanged(NaviController arg0) {
		// TODO 自動生成されたメソッド・スタブ
		//目的地までの残り距離
		double rema_dist = mNaviController.getTotalDistance();

		//目的地までの残りの時間
		double rema_time = mNaviController.getTotalTime();

		//出発地から目的地までの距離
		double total_dist = mNaviController.getDistanceOfRemainder();

		//出発地から目的地までの時間
		double total_time = mNaviController.getTimeOfRemainder();

		//現在位置
		Location location = mNaviController.getLocation();
		return false;
	}

	@Override
	public boolean onLocationTimeOver(NaviController arg0) {
		// TODO 自動生成されたメソッド・スタブ
		return false;
	}

	@Override
	public boolean onRouteOut(NaviController arg0) {
		// TODO 自動生成されたメソッド・スタブ
		return false;
	}

}
