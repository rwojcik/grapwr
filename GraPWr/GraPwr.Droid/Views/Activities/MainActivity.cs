using Android.App;
using Android.OS;
using GraPwr.Droid.Views.Fragments;

namespace GraPwr.Droid.Views.Activities
{
	[Activity (Label = "Gra Pwr", MainLauncher = true, Icon = "@drawable/icon")]
	public class MainActivity : Activity
	{
		protected override void OnCreate (Bundle savedInstanceState)
		{
			base.OnCreate (savedInstanceState);

			SetContentView (Resource.Layout.Activity_Main);

            if (savedInstanceState == null)
            {
                FragmentManager.BeginTransaction().Add(Resource.Id.container, new PlaceholderFragment()).Commit();
            }
        }
	}
}


