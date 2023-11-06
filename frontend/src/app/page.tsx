import IntegrationForm from "./Components/IntegrationForm";

export default function Home() {
  return (
    <main className="flex min-h-screen flex-col items-center justify-between p-16 bg-sky-900">
      <div className="flex w-4/5 flex-row items-center justify-between p-2">
        <div className="w-full m-5 rounded-3xl bg-sky-950 flex flex-col items-center justify-between p-2">
          <h1 className="lg:text-4xl mb-5 mx-10 mt-5 text-4xl font-extrabold">
            Get integration
          </h1>
          <div className="rounded-3xl w-full h-20 pb-5 mb-2 bg-slate-200"></div>
        </div>
        <div className="w-full m-5 rounded-3xl bg-sky-950 flex flex-col items-center justify-between p-2">
          <h1 className="lg:text-4xl mb-5 mx-10 mt-5 text-4xl font-extrabold">
            Post integration!!
          </h1>
          <IntegrationForm />
        </div>
      </div>
    </main>
  );
}
