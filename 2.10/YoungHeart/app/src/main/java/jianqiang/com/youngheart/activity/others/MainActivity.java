package jianqiang.com.youngheart.activity.others;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;

import java.util.ArrayList;
import java.util.Date;

import jianqiang.com.mylibrary.net.RemoteService;
import jianqiang.com.mylibrary.net.RequestParameter;
import jianqiang.com.youngheart.R;
import jianqiang.com.youngheart.activity.personcenter.ListDemoActivity;
import jianqiang.com.youngheart.base.AppBaseActivity;
import jianqiang.com.youngheart.engine.AppConstants;
import jianqiang.com.youngheart.engine.GlobalVariables;
import jianqiang.com.youngheart.engine.YoungHeartApplication;
import jianqiang.com.youngheart.entity.CinemaBean;
import jianqiang.com.youngheart.entity.Movie;
import jianqiang.com.youngheart.entity.MovieList;
import rx.Observable;
import rx.Subscriber;
import rx.schedulers.Schedulers;

public class MainActivity extends AppBaseActivity {
	private Button btnLogin;
	private Button btnLoginNew;

	private Button btnListDemo;

	private Button btnTestOKHttpGet;
	private Button btnTestOKHttpPost;

	private TextView tvMovie;

	private Button btnSendReport;

	private Button btnTestOKHttpGetList;
	private Button btnTestOKHttpGetListForce;

	private Button btnGetCurrentTime;

	@Override
	protected void initVariables() {

	}

	@Override
	protected void initViews(Bundle savedInstanceState) {
		setContentView(R.layout.activity_main);

		int a = 1;


//		取数据，首先检查内存是否有缓存 然后检查文件缓存中是否有
//		最后才从网络中取
//		前面任何一个条件满足，就不会执行后面的
		final String[] memoryCache = new String[1];

		final Observable<String> memory = Observable.create(new Observable.OnSubscribe<String>() {
			@Override
			public void call(Subscriber<? super String> subscriber) {
				if (memoryCache != null) {
					subscriber.onNext(memoryCache[0]);
				} else {
					subscriber.onCompleted();
				}
			}
		});
		Observable<String> disk = Observable.create(new Observable.OnSubscribe<String>() {
			@Override
			public void call(Subscriber<? super String> subscriber) {
//				String cachePref = rxPreferences.getString("cache").get();
				String cachePref = "abcd";
				if (!TextUtils.isEmpty(cachePref)) {
					subscriber.onNext(cachePref);
				} else {
					subscriber.onCompleted();
				}
			}
		});

		Observable<String> network = Observable.just("network");

		//主要就是靠concat operator来实现
		Observable.concat(memory, disk, network)
				.first()
				.subscribeOn(Schedulers.newThread())
				.subscribe(s -> {
					memoryCache[0] = "memory";
					System.out.println("--------------subscribe: " + s);
				});





		btnLogin = (Button) findViewById(R.id.btnLogin);
		btnLogin.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(MainActivity.this,
						LoginActivity.class);
				intent.putExtra(AppConstants.Email, "jianqiang.bao@qq.com");

				CinemaBean cinema = new CinemaBean();
				cinema.setCinemaId("1");
				cinema.setCinemaName("星美");

				//使用全局变量的方式传递参数
				GlobalVariables.Cinema = cinema;

				startActivity(intent);
			}
		});

		btnLoginNew = (Button) findViewById(R.id.btnLoginNew);
		btnLoginNew.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(MainActivity.this,
						LoginNewActivity.class);
				intent.putExtra(AppConstants.Email, "jianqiang.bao@qq.com");

				CinemaBean cinema = new CinemaBean();
				cinema.setCinemaId("1");
				cinema.setCinemaName("星美");

				//使用intent上挂可序列化实体的方式传递参数
				intent.putExtra(AppConstants.Cinema, cinema);

				startActivity(intent);
			}
		});


		btnListDemo = (Button) findViewById(R.id.btnListDemo);
		btnListDemo.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(MainActivity.this,
						ListDemoActivity.class);
				startActivity(intent);
			}
		});

		btnTestOKHttpGet = (Button) findViewById(R.id.btnTestOKHttpGet);
		btnTestOKHttpGet.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				testHttpGet();
			}
		});


		btnTestOKHttpPost = (Button) findViewById(R.id.btnTestOKHttpPost);
		btnTestOKHttpPost.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				testHttpPost();
			}
		});


		tvMovie = (TextView)findViewById(R.id.tvMovie);

		btnSendReport = (Button) findViewById(R.id.btnSendReport);
		btnSendReport.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				testSendReport();
			}
		});

		btnTestOKHttpGetList = (Button) findViewById(R.id.btnTestOKHttpGetList);
		btnTestOKHttpGetList.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				testOKHttpGetList();
			}
		});


		btnTestOKHttpGetListForce = (Button) findViewById(R.id.btnTestOKHttpGetListForce);
		btnTestOKHttpGetListForce.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				testOKHttpGetListForce();
			}
		});

		btnGetCurrentTime = (Button) findViewById(R.id.btnGetCurrentTime);
		btnGetCurrentTime.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				String strCurrentTime = new Date(System.currentTimeMillis() + YoungHeartApplication.delta).toString();
				tvMovie.setText(strCurrentTime);
			}
		});

	}

	@Override
	protected void loadData() {

	}

	void testHttpGet() {
		ArrayList<RequestParameter> params = new ArrayList<RequestParameter>();
		RequestParameter rp1 = new RequestParameter("movieId", "1");
		params.add(rp1);

		RemoteService.getInstance().invoke("getMovieDetail", params, new AbstractRequectCallback() {

			@Override
			public void onSuccess(String content) {
				super.onSuccess(content);

				Movie movie = JSON.parseObject(content, Movie.class);
				tvMovie.setText(movie.movieName);

				testOKHttpGetList();
			}
		});
	}

	void testOKHttpGetList() {
		ArrayList<RequestParameter> params = new ArrayList<RequestParameter>();
		RequestParameter rp1 = new RequestParameter("cityId", "1");
		params.add(rp1);
		RequestParameter rp2 = new RequestParameter("showdate", "20161101");
		params.add(rp2);

		RemoteService.getInstance().invoke("getMovieList", params, new AbstractRequectCallback() {

			@Override
			public void onSuccess(String content) {
				super.onSuccess(content);

				MovieList entity = JSON.parseObject(content, MovieList.class);
				tvMovie.setText(entity.movieList.get(0).movieName);
			}
		});
	}

	//强制更新
	void testOKHttpGetListForce() {
		ArrayList<RequestParameter> params = new ArrayList<RequestParameter>();
		RequestParameter rp1 = new RequestParameter("cityId", "1");
		params.add(rp1);
		RequestParameter rp2 = new RequestParameter("showdate", "20161101");
		params.add(rp2);

		RemoteService.getInstance().invoke("getMovieList", params, new AbstractRequectCallback() {

			@Override
			public void onSuccess(String content) {
				super.onSuccess(content);

				MovieList entity = JSON.parseObject(content, MovieList.class);
				tvMovie.setText(entity.movieList.get(0).movieName);
			}
		}, true);
	}

	void testHttpPost() {
		ArrayList<RequestParameter> params = new ArrayList<RequestParameter>();
		RequestParameter rp1 = new RequestParameter("username", "baobao");
		params.add(rp1);
		RequestParameter rp2 = new RequestParameter("password", "123456");
		params.add(rp2);

		RemoteService.getInstance().invoke("registerUser", params, new AbstractRequectCallback() {

			@Override
			public void onSuccess(String content) {
				super.onSuccess(content);

				//dosomething
				String a = "";
			}

			@Override
			public void onFail(String errorMessage) {
				dlg.dismiss();
				//有自己的特殊逻辑,不需要继承AppBaseActivity的onFail方法
			}
		});
	}

	void testSendReport() {
		ArrayList<RequestParameter> params = new ArrayList<RequestParameter>();
		RequestParameter rp1 = new RequestParameter("cityId", "1");
		params.add(rp1);
		RequestParameter rp2 = new RequestParameter("cityId", "2");
		params.add(rp2);
		RequestParameter rp3 = new RequestParameter("cityId", "3");
		params.add(rp3);

		RemoteService.getInstance().invoke("sendReport", params);
	}
}