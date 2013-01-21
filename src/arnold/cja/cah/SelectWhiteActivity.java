package arnold.cja.cah;import android.app.AlertDialog;import android.app.Dialog;import android.app.ListActivity;import android.content.DialogInterface;import android.content.Intent;import android.os.Bundle;import android.util.Log;import android.view.ContextMenu;import android.view.ContextMenu.ContextMenuInfo;import android.view.MenuInflater;import android.view.MenuItem;import android.view.View;import android.widget.AdapterView.AdapterContextMenuInfo;import android.widget.ArrayAdapter;import android.widget.ListView;import android.widget.TextView;import android.widget.TextView.BufferType;import arnold.cja.cah.R;/** * This activity shows a Player his/her white cards * as well as the current black card.  As the player taps * white cards, the text of the white card fills in the next * available blank in the black card.  Clicking submit submits * the Combo to the CardCzar.  Clicking clear brings all white cards * back into the player's hand. */public class SelectWhiteActivity extends ListActivity {   private static final String TAG = "SelectWhiteActivity";   private static final int FINISH_SELECTION_DIALOG = 1;   private ArrayAdapter<Card> mAdapter;	   @Override   public void onCreate(Bundle savedInstanceState) {      super.onCreate(savedInstanceState);      Log.i(TAG, "SelectWhite::onCreate");      if (!Util.constructGameManagerIfNecessary(this)) { return; }      mAdapter = new CardSetArrayAdapter(this, LaunchActivity.gm.getActivePlayer().getWhiteHand().getAsArrayList());      setListAdapter(mAdapter);      setContentView(R.layout.select_white);      this.registerForContextMenu(this.getListView());      updateComboText();	    }   @Override   public void onCreateContextMenu(ContextMenu menu, View v,         ContextMenuInfo menuInfo) {      super.onCreateContextMenu(menu, v, menuInfo);      MenuInflater inflater = getMenuInflater();      inflater.inflate(R.menu.define_phrase, menu);      Log.i(TAG, "Created mContext menu in SelectWhiteActivity");   }   @Override   public boolean onContextItemSelected(MenuItem item) {      Log.i(TAG, "onContextItemSelected");      AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();      Card card = (Card) getListAdapter().getItem((int) info.id);      Intent defineIntent = card.getDefinitionIntent(item.getItemId());      startActivity(defineIntent);      return true;   }   void updateComboText() {      TextView t=(TextView)findViewById(R.id.whiteforblack);       t.setText(LaunchActivity.gm.getActivePlayer().getComboInProgress().getStyledStatement(), BufferType.SPANNABLE);   }   @Override   protected Dialog onCreateDialog(int id) {      AlertDialog.Builder builder = new AlertDialog.Builder(this);      switch(id) {      case FINISH_SELECTION_DIALOG:         String text = "Please finish your selection before submitting!";         builder.setMessage(text)         .setCancelable(false)         .setPositiveButton("OK", new DialogInterface.OnClickListener() {            public void onClick(DialogInterface dialog, int id) {            }         });         return builder.create();      default:         return null;      }   }   public void clear() {      Player p = LaunchActivity.gm.getActivePlayer();      Combo c = p.getComboInProgress();      while(!c.empty()) {         p.getWhiteHand().add(c.popWhite());      }      mAdapter.notifyDataSetChanged();      updateComboText();   }   public void onClear(View v) {      clear();   }   public void onSubmit(View v) {      Player p = LaunchActivity.gm.getActivePlayer();      Combo c = p.getComboInProgress();      if (c.isComplete()) {         p.setHasPickedWhite(true);         Intent intent=new Intent();         setResult(RESULT_OK, intent);         LaunchActivity.gm.setLeavingActivity();         finish();      }      else {         showDialog(FINISH_SELECTION_DIALOG);      }   }   @Override   protected void onListItemClick(ListView l, View v, int position, long id) {      Card card = (Card) getListAdapter().getItem(position);      Player p = LaunchActivity.gm.getActivePlayer();      Combo  c = p.getComboInProgress();      if (!c.isComplete()) {         c.addWhiteCard(card);         p.getWhiteHand().remove(card);         updateComboText();         mAdapter.notifyDataSetChanged();      }   }   @Override   protected void onPause() {      super.onPause();      Log.i(TAG, "SelectWhiteActivity::onPause");      Util.saveStateIfLeavingApp(this);   }   @Override   public void onBackPressed() {      LaunchActivity.gm.setLeavingActivity();      super.onBackPressed();   }}