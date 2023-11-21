import Logo from "../Components/Logo";
import LeaderBoard from "../Metric/LeaderBoard";

export default function Page() {
  const logo = Logo("rgb(134 239 172)", "white")
  const leaderboard = LeaderBoard();
  
  return (
    <main className="bg-green-50 grid h-screen grid-cols-8 grid-rows-6 gap-4">
        <div className="bg-green-700 col-span-8 grid grid-cols-8 grid-rows-2 gap-2">
            <div className="flex flex-row items-center justify-between col-span-2 ml-2">{logo}</div>
            <div className="col-span-6"></div>
            <div className="col-span-1"></div>
            <a className="flexCenterC justify-center col-span-1 bg-green-50 rounded-2xl my-1 text-black" href="/login">
                <h1 className="text-sm font-extrabold text-black">Home</h1> 
            </a>
            
            <div className="col-span-1 bg-green-50 rounded-2xl my-1"></div>
            <div className="col-span-1 bg-green-50 rounded-2xl my-1"></div>
            <div className="col-span-1 bg-green-50 rounded-2xl my-1"></div>
            <div className="col-span-1 bg-green-50 rounded-2xl my-1"></div>
            <div className="col-span-1 bg-green-50 rounded-2xl my-1"></div>
        </div>
        <div className="row-start-3 p-1 col-span-6 row-span-4 grid grid-cols-4 grid-rows-3 gap-1 mx-8 mb-4 bg-green-50 border-green-700 border-4 rounded-lg my-1">
            {leaderboard}
            <div className="col-span-1 bg-green-100 rounded-md flexCenterC justify-center text-green-700 text-5xl">+</div>
            <div className="col-span-1 bg-green-100 rounded-md flexCenterC justify-center text-green-700 text-5xl">+</div>
            <div className="col-span-1 bg-green-100 rounded-md flexCenterC justify-center text-green-700 text-5xl">+</div>
            <div className="col-span-1 bg-green-100 rounded-md flexCenterC justify-center text-green-700 text-5xl">+</div>
            <div className="col-span-1 bg-green-100 rounded-md flexCenterC justify-center text-green-700 text-5xl">+</div>
            <div className="col-span-1 bg-green-100 rounded-md flexCenterC justify-center text-green-700 text-5xl">+</div>
            <div className="col-span-1 bg-green-100 rounded-md flexCenterC justify-center text-green-700 text-5xl">+</div>
            <div className="col-span-1 bg-green-100 rounded-md flexCenterC justify-center text-green-700 text-5xl">+</div>

        </div>
        <div className="p-1 row-start-2 col-span-2 row-span-5 mx-8 mb-4 bg-green-50 border-green-700 border-4 rounded-lg my-1 overflow-y-scroll max-h-max">
            <div className="col-span-1 bg-green-100 rounded-md flexCenterC justify-center text-green-700 text-5xl h-1/4 mb-1">+</div>
            <div className="col-span-1 bg-green-100 rounded-md flexCenterC justify-center text-green-700 text-5xl h-1/4 mb-1">+</div>
            <div className="col-span-1 bg-green-100 rounded-md flexCenterC justify-center text-green-700 text-5xl h-1/4 mb-1">+</div>
            <div className="col-span-1 bg-green-100 rounded-md flexCenterC justify-center text-green-700 text-5xl h-1/4 mb-1">+</div>
            <div className="col-span-1 bg-green-100 rounded-md flexCenterC justify-center text-green-700 text-5xl h-1/4 mb-1">+</div>
            <div className="col-span-1 bg-green-100 rounded-md flexCenterC justify-center text-green-700 text-5xl h-1/4 mb-1">+</div>  
            
        </div>

    </main>
  );
}
  