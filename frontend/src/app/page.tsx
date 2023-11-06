import Image from 'next/image'

export default function Home() {
  return (
    <main className="flex min-h-screen flex-col items-center justify-between p-16 bg-sky-900">
      <div className="flex w-4/5 flex-row items-center justify-between p-2">
        <div className="w-full m-5 rounded-3xl bg-sky-950 flex flex-col items-center justify-between p-2">
          <h1 className="lg:text-4xl mb-5 mx-10 mt-5 text-4xl font-extrabold">
            Get integration
          </h1>
          <div className="rounded-3xl w-full h-20 pb-5 mb-2 bg-slate-200"></div>
          <button className="bg-sky-50 bg-opacity-50 hover:bg-blue-900 text-white font-bold py-2 px-4 rounded-full mb-5">
            Get button
          </button>
        </div>
        <div className="w-full m-5 rounded-3xl bg-sky-950 flex flex-col items-center justify-between p-2">
          <h1 className="lg:text-4xl mb-5 mx-10 mt-5 text-4xl font-extrabold">
            Post integration
          </h1>
          <form>
            <label className="mx-2">ID:
              <input className="w-full rounded-3xl mb-3 text-black py-1 px-2" type="text" />
            </label>
            <label className="mx-2">Title:
              <input className="w-full rounded-3xl mb-3 text-black py-1 px-2" type="text" />
            </label>
            <input className="bg-sky-50 bg-opacity-50 hover:bg-blue-900 text-white font-bold py-2 px-4 rounded-full mb-5" type="submit" value="Post Integration"/>
          </form>
        </div>
      </div>
    </main>
  )
}
