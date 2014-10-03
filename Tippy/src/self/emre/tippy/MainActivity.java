package self.emre.tippy;

import java.util.HashMap;
import java.util.Map;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;

public class MainActivity extends Activity {

	private EditText editText;
	private RadioButton tenPctRadio;
	private RadioButton fifteenPctRadio;
	private RadioButton twentyPctRadio;
	private TextView totalTextView;
	private TextView divideByTextView;
	private EditText enterNumPeopleText;
	private Button divideByButton;
	private TextView amountPerPersonTextView;
	
	private Map<Float, Float> tipRatioMap = new HashMap<>();
	private float totalAmount = 0f;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		if (savedInstanceState == null) {
			getFragmentManager().beginTransaction()
					.add(R.id.container, new PlaceholderFragment()).commit();
		}
	}
	
	@Override
	protected void onStart() {
		super.onStart();
		
		this.editText = (EditText) findViewById(R.id.enter_bill_amount);
		
		this.tenPctRadio = (RadioButton) findViewById(R.id.ten_pct_radio);
		this.fifteenPctRadio = (RadioButton) findViewById(R.id.fifteen_pct_radio);
		this.twentyPctRadio = (RadioButton) findViewById(R.id.twenty_pct_radio);
		setRadioButtonsVisible(false);
		
		this.totalTextView = (TextView) findViewById(R.id.total_text_view);
		this.totalTextView.setVisibility(View.INVISIBLE);	
		
		this.divideByTextView = (TextView) findViewById(R.id.divide_by_text_view);
		this.divideByTextView.setVisibility(View.INVISIBLE);
		
		this.enterNumPeopleText = (EditText) findViewById(R.id.enter_num_people);
		this.enterNumPeopleText.setVisibility(View.INVISIBLE);
		
		this.divideByButton = (Button) findViewById(R.id.divide_by_button);
		this.divideByButton.setVisibility(View.INVISIBLE);
		
		this.amountPerPersonTextView = (TextView) findViewById(R.id.amount_per_person_text_view);
		this.amountPerPersonTextView.setVisibility(View.INVISIBLE);
	}
	
	@Override
	protected void onStop() {
		super.onStop();
		
		this.editText.setText("");
		
		clearRadioButtons();
		setRadioButtonsVisible(false);
		
		this.totalTextView.setText("");
		this.totalTextView.setVisibility(View.INVISIBLE);
		
		this.divideByTextView.setVisibility(View.INVISIBLE);
		
		this.enterNumPeopleText.setText("");
		this.enterNumPeopleText.setVisibility(View.INVISIBLE);
		
		this.divideByButton.setVisibility(View.INVISIBLE);
		
		this.amountPerPersonTextView.setText("");
		this.amountPerPersonTextView.setVisibility(View.INVISIBLE);
	}
	
	private void setRadioButtonsVisible(boolean visible) {
		int vis = visible ? View.VISIBLE : View.INVISIBLE;
		this.tenPctRadio.setVisibility(vis);
		this.fifteenPctRadio.setVisibility(vis);
		this.twentyPctRadio.setVisibility(vis);
	}
	
	private void clearRadioButtons() {
		clearRadioButton(this.tenPctRadio);
		clearRadioButton(this.fifteenPctRadio);
		clearRadioButton(this.twentyPctRadio);
	}
	
	private void clearRadioButton(RadioButton rd) {
		rd.setText("");
		rd.setChecked(false);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	public void divideBy(View view) {	
		String numPeopleText = this.enterNumPeopleText.getText().toString();
		if (numPeopleText != null && numPeopleText.trim().length() > 0) {
			int numPeople = Integer.parseInt(numPeopleText);
			if (numPeople > 0) {
				float amountPerPerson = totalAmount / (float) numPeople;
				this.amountPerPersonTextView.setText(String.format("%.2f", amountPerPerson) + " per person");
				this.amountPerPersonTextView.setVisibility(View.VISIBLE);
			}
		}
	}
	
	public void calculateTip(View view) {
		String billAmtStr = this.editText.getText().toString();	
		float billAmount = Float.parseFloat(billAmtStr);
		
		this.tenPctRadio.setText("10% = " + getTipAmount(billAmount, 0.10f));
		this.fifteenPctRadio.setText("15% = " + getTipAmount(billAmount, 0.15f));
		this.twentyPctRadio.setText("20% = " + getTipAmount(billAmount, 0.20f));
		
		setRadioButtonsVisible(true);
	}
	
	private void setTotalText(float tipRatio) {
		float billAmount = Float.parseFloat(this.editText.getText().toString());	
		Float tip = tipRatioMap.get(tipRatio);
		if (tip != null) {
			this.totalAmount = billAmount + tip.floatValue();
			this.totalTextView.setText("Total = " + String.format("%.2f", this.totalAmount));
			this.totalTextView.setVisibility(View.VISIBLE); 
			
			this.divideByTextView.setVisibility(View.VISIBLE);
			this.enterNumPeopleText.setVisibility(View.VISIBLE);	
			this.divideByButton.setVisibility(View.VISIBLE);
		}
	}
	
	public void onRadioButtonClicked(View view) {
	    boolean checked = ((RadioButton) view).isChecked();
	    
	    switch(view.getId()) {
	        case R.id.ten_pct_radio:
	            if (checked) {
	            	setTotalText(0.10f);
	            }	                
	            break;
	        case R.id.fifteen_pct_radio:
	            if (checked) {
	            	setTotalText(0.15f);
	            }	                
	            break;
	        case R.id.twenty_pct_radio:
	        	if (checked) {
	        		setTotalText(0.20f);
	        	}
	        	break;
	    }
	}

	private String getTipAmount(float billAmount, float tipRatio) {
		float roundedTip = (float) Math.round(billAmount * tipRatio * 100) / 100;
		tipRatioMap.put(tipRatio, roundedTip);
		return String.format("%.2f", roundedTip);
	}
	
	/**
	 * A placeholder fragment containing a simple view.
	 */
	public static class PlaceholderFragment extends Fragment {

		public PlaceholderFragment() {
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			View rootView = inflater.inflate(R.layout.fragment_main, container,
					false);
			return rootView;
		}
	}

}
