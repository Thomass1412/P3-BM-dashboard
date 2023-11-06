import Image from 'next/image'

export default function Home() {
  return (
    <main className="flex min-h-screen flex-col items-center justify-between p-24 bg-sky-900">
      <div className="bg-sky-950 flex flex-col items-center justify-between p-2">
        <h1 className="lg:text-4xl mb-10 mx-10 mt-5 text-4xl font-extrabold">
          Get integration
        </h1>
        <div className="w-full h-20 pb-5"></div>
        <button className="bg-sky-50 bg-opacity-50 hover:bg-blue-900 text-white font-bold py-2 px-4 rounded-full">
          Get button
        </button>
        <div>Hej2</div>
      </div>
    </main>
  )
}
