using Android.App;
using Android.OS;
using Android.Views;

namespace GraPwr.Droid.Views.Fragments
{
    public class PlaceholderFragment : Fragment
    {
        public override View OnCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
        {
            var rootView = inflater.Inflate(Resource.Layout.Fragment_Main, container, false);
            return rootView;
        }
    }
}