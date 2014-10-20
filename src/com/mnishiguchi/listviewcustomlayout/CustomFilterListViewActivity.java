package com.mnishiguchi.listviewcustomlayout;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

/**
 * Android ListView with Custom Layout and Filter example
 * http://www.mysamplecode.com/2012/07/android-listview-custom-layout-filter.html
 */
public class CustomFilterListViewActivity  extends Activity
{
	// INSTANCE VARIABLE
	private CustomArrayAdapter mDataAdapter = null;

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
	
		displayListView();
	}

	/**
	 * Display a list view with a filter text field.
	 */
	private void displayListView()
	{
		// Prepare a list of countries data
		ArrayList<Country> countryList = new ArrayList<Country>();
		Country country = new Country("AFG","Afghanistan","Asia", "Southern and Central Asia");
		countryList.add(country);
		country = new Country("ALB","Albania","Europe","Southern Europe");
		countryList.add(country);
		country = new Country("DZA","Algeria","Africa","Northern Africa");
		countryList.add(country);
		country = new Country("ASM","American Samoa","Oceania","Polynesia");
		countryList.add(country);
		country = new Country("AND","Andorra","Europe","Southern Europe");
		countryList.add(country);
		country = new Country("AGO","Angola","Africa","Central Africa");
		countryList.add(country);
		country = new Country("AIA","Anguilla","North America","Caribbean");
		countryList.add(country);
		country = new Country("JPN","Japan","Asia","East Asia");
		countryList.add(country);
		country = new Country("USA","United States of America", "North America", "North America");
		countryList.add(country);
	
		// Create an adapter.
		mDataAdapter = new CustomArrayAdapter(this, R.layout.list_item_countries, countryList);
		
		/* ListView settings */ 
		
		ListView listView = (ListView) findViewById(R.id.listView1);
		listView.setAdapter(mDataAdapter);
		listView.setTextFilterEnabled(true);  // Enables filtering.
		listView.setOnItemClickListener(new OnItemClickListener() {
			
			public void onItemClick(AdapterView<?> parent, View view, int position, long id)
			{
				// When clicked, show a toast with the TextView text.
				Country country = (Country) parent.getItemAtPosition(position);
				Toast.makeText(getApplicationContext(),
						country.getCode(), Toast.LENGTH_SHORT).show();
			}
		} );

		/* Filter text field settings */ 
		
		EditText etFilter = (EditText) findViewById(R.id.et_filter);
		etFilter.addTextChangedListener(new TextWatcher() {

			public void afterTextChanged(Editable s) {  }  // Unused.
			
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {  }  // Unused.
			
			public void onTextChanged(CharSequence input, int start, int before, int count)
			{
				// Call the filter with user's input.
				mDataAdapter.getFilter().filter(input);
			}
		} );
	}

	/**
	 * A custom adapter with a custom filter, designed specifically for the countries data.
	 */
	private class CustomArrayAdapter extends ArrayAdapter<Country>
	{
		// INSTANCE VARIABLES
		private ArrayList<Country> mOriginalList;  // Remember the original list.
		private ArrayList<Country> mFilteredList;  // Remember the filtered list.
		private CountryFilter mFilter;  // A custom filter

		/** CONSTRUCTOR */
		public CustomArrayAdapter(Context context,
				int textViewResourceId, ArrayList<Country> countryList)
		{
			// super constructor
			super(context, textViewResourceId, countryList);
			
			// Remember all the elements of the passed-in list. 
			this.mOriginalList = new ArrayList<Country>();
			this.mOriginalList.addAll(countryList);
			
			// Initialize the filtered list, initially all the elements of the passed-in list. 
			this.mFilteredList = new ArrayList<Country>();
			this.mFilteredList.addAll(countryList);
		}

		@Override
		public Filter getFilter()
		{
			// Create only one instance of the CountryFilter.
			if (mFilter == null)
			{
				mFilter  = new CountryFilter();
			}
			return mFilter;
		}

		/** 
		 * A ViewHolder stores the TextViews of a list item.
		 * It can be attached to a View as a tag.
		 */
		private class ViewHolder
		{
			TextView code;
			TextView name;
			TextView continent;
			TextView region;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent)
		{
			ViewHolder holder = null;
			Log.v("ConvertView", String.valueOf(position) );
			
			// If the recycled view is not provided, create a new one.
			if (convertView == null)
			{
				LayoutInflater vi = (LayoutInflater)
						getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				convertView = vi.inflate(R.layout.list_item_countries, null);
				
				// Get references to the TextViews and store them in a VewHolder object.
				holder = new ViewHolder();
				holder.code = (TextView) convertView.findViewById(R.id.code);
				holder.name = (TextView) convertView.findViewById(R.id.name);
				holder.continent = (TextView) convertView.findViewById(R.id.continent);
				holder.region = (TextView) convertView.findViewById(R.id.region);

				// Attach the ViewHolder to the view as a tag.
				convertView.setTag(holder);
			}
			else
			{
				// Get the ViewHolder object attached to the recycled view.
				holder = (ViewHolder) convertView.getTag();
			}
			
			// Get data for this position.
			Country country = mFilteredList.get(position);
			
			// Set the data text on each TextView.
			holder.code.setText(country.getCode() );
			holder.name.setText(country.getName() );
			holder.continent.setText(country.getContinent() );
			holder.region.setText(country.getRegion() );
			
			return convertView;
		}

		/**
		 * A custom filter that is designed specifically for the countries data.
		 * Case-insensitive.
		 */
		private class CountryFilter extends Filter
		{
			/* No instance variables*/
			
			/*
			 * Invoked in a worker thread to filter the data according to the constraint.
			 * Performs the filtering operation.
			 * Returns a FilterResults object which will then be published in the UI thread
			 * through the publishResults method.
			 */
			@Override
			protected FilterResults performFiltering(CharSequence constraint)
			{
				// Create a filter result object.
				FilterResults result = new FilterResults();
				
				// Ensure that constraint exists and its length is greater than zero.
				if (constraint != null && constraint.length() > 0)
				{
					// Filter the original list with the constraint (user-entered filter constraint string).
					ArrayList<Country> filteredItems = new ArrayList<Country>();
					for (Country country : mOriginalList)
					{
						if (country.toString().toLowerCase().contains(
								constraint.toString().toLowerCase() ) )  // Case-insensitive.
						{
							filteredItems.add(country);
						}
					}
					
					// Set the result data.
					result.count = filteredItems.size();
					result.values = filteredItems;
				}
				else  // When the constraint is null, the original data must be restored.
				{
					// The synchronized keyword is used to keep variables or methods thread-safe.
					synchronized(this)
					{
						// Set the result to the original data.
						result.values = mOriginalList;
						result.count = mOriginalList.size();
					}
				}
				return result;
			}

			/*
			 * Invoked in the UI thread to publish the FilterResults.
			 * Displays the results computed in the performFiltering method.
			 */
			@SuppressWarnings("unchecked")
			@Override
			protected void publishResults(CharSequence constraint, FilterResults results)
			{
				// Remember the filtered data.
				mFilteredList = (ArrayList<Country>) results.values;
				
				// Notifies the attached observers that the underlying data has been changed
				// and any View reflecting the data set should refresh itself.
				(CustomArrayAdapter.this).notifyDataSetChanged();
				
				// Clear the adapter's list and add the filtered data to it.
				(CustomArrayAdapter.this).clear();  // Clear data in the adapter.
				for (int i = 0, len = mFilteredList.size(); i < len; i++)
				{
					(CustomArrayAdapter.this).add(mFilteredList.get(i) );  // Add data to the adapter.
				}
				
				// Notifies the attached observers that the underlying data is no longer valid or available.
				// Once invoked this adapter is no longer valid and should not report further data set changes.
				(CustomArrayAdapter.this).notifyDataSetInvalidated();
			}
		}
	}
}
