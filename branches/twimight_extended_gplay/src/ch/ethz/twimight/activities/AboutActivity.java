/*******************************************************************************
 * Copyright (c) 2011 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/gpl-2.0.html
 * 
 * Contributors:
 *     Paolo Carta - Implementation
 *     Theus Hossmann - Implementation
 *     Dominik Schatzmann - Message specification
 ******************************************************************************/
package ch.ethz.twimight.activities;

import java.util.Date;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import ch.ethz.twimight.R;
import ch.ethz.twimight.net.tds.TDSService;
import ch.ethz.twimight.security.CertificateManager;

/**
 * Show information about Twimight
 * @author thossmann
 *
 */
public class AboutActivity extends TwimightBaseActivity{

	public static final String TAG = "AboutActivity";
	Button revokeButton;
	Button updateButton;
	TextView lastUpdate;
	TextView keyOk;
	TextView versionName;
	
	/** 
	 * Called when the activity is first created. 
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.showabout);
		
			
		keyOk = (TextView) findViewById(R.id.showAboutKeys);
		revokeButton = (Button) findViewById(R.id.showAboutRevoke);
		CertificateManager cm = new CertificateManager(this);
		if(cm.hasCertificate()){
			keyOk.setText(getString(R.string.ok));
			revokeButton.setOnClickListener(new OnClickListener(){
				@Override
				public void onClick(View v) {
					showRevokeDialog();
				}
			});
		} else {
			revokeButton.setVisibility(Button.GONE);
			keyOk.setText(getString(R.string.no_certificate));
		}
		
		updateButton = (Button) findViewById(R.id.showAboutUpdate);
		lastUpdate = (TextView) findViewById(R.id.showAboutLastUpdate);
		long lastTimestamp = TDSService.getLastUpdate(this);
		if(lastTimestamp == 0){
			lastUpdate.setText(getText(R.string.no_update));
		} else {
			Date date = new Date(lastTimestamp);
			
			lastUpdate.setText(DateFormat.format("MM/dd/yy ", date));
		}
		updateButton.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				Intent updateIntent = new Intent(getBaseContext(), TDSService.class);
				updateIntent.putExtra("synch_request", TDSService.SYNCH_ALL_FORCE);
				startService(updateIntent);
				Toast.makeText(getBaseContext(), getString(R.string.starting_update), Toast.LENGTH_LONG).show();
				finish();
			}
			
		});
		
		
		versionName = (TextView) findViewById(R.id.showAboutVersion);
		try
		{
		    String appVer = this.getPackageManager().getPackageInfo(this.getPackageName(), 0).versionName;
		    versionName.setText(appVer);
		}
		catch (NameNotFoundException e)
		{
		   
		}
	}
	
	/**
	 * on Resume
	 */
	@Override
	public void onResume(){
		super.onResume();

	}

	/**
	 * Called at the end of the Activity lifecycle
	 */
	@Override
	public void onDestroy(){
		super.onDestroy();
		
		revokeButton.setOnClickListener(null);
		updateButton.setOnClickListener(null);
		
		unbindDrawables(findViewById(R.id.showAboutRoot));


	}
	
	/**
	 * Asks the user if she really wants to revoke the key
	 */
	private void showRevokeDialog(){
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage(getString(R.string.revoke_key))
		       .setCancelable(false)
		       .setPositiveButton(getString(R.string.yes), new DialogInterface.OnClickListener() {
		           public void onClick(DialogInterface dialog, int id) {
						Intent updateIntent = new Intent(getBaseContext(), TDSService.class);
						updateIntent.putExtra("synch_request", TDSService.SYNCH_REVOKE);
						startService(updateIntent);
						Toast.makeText(getBaseContext(), getString(R.string.revoking), Toast.LENGTH_LONG).show();
						finish();
		           }
		       })
		       .setNegativeButton(getString(R.string.no), new DialogInterface.OnClickListener() {
		           public void onClick(DialogInterface dialog, int id) {
		                dialog.cancel();
		           }
		       });
		AlertDialog alert = builder.create();
		alert.show();
	}
	
}