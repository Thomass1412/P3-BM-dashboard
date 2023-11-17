import { log } from "console";
import Logo from "./Components/SlideMenu";
import Background from "frontend\public\BMbackground.jpg"
import { Table } from 'flowbite-react';

export default function Home() {
    return (
        <main className="bg-white">
            <Table>
                <Table.Head>
                    <Table.HeadCell>Product name</Table.HeadCell>
                    <Table.HeadCell>Color</Table.HeadCell>
                    <Table.HeadCell>Category</Table.HeadCell>
                    <Table.HeadCell>Price</Table.HeadCell>
                    <Table.HeadCell>
                    <span className="sr-only">Edit</span>
                    </Table.HeadCell>
                </Table.Head>
                <Table.Body className="divide-y">
                    <Table.Row className="bg-white dark:border-gray-700 dark:bg-gray-800">
                    <Table.Cell className="whitespace-nowrap font-medium text-gray-900 dark:text-white">
                        {'Apple MacBook Pro 17"'}
                    </Table.Cell>
                    <Table.Cell>Sliver</Table.Cell>
                    <Table.Cell>Laptop</Table.Cell>
                    <Table.Cell>$2999</Table.Cell>
                    <Table.Cell>
                        <a href="#" className="font-medium text-cyan-600 hover:underline dark:text-cyan-500">
                        Edit
                        </a>
                    </Table.Cell>
                    </Table.Row>
                    <Table.Row className="bg-white dark:border-gray-700 dark:bg-gray-800">
                    <Table.Cell className="whitespace-nowrap font-medium text-gray-900 dark:text-white">
                        Microsoft Surface Pro
                    </Table.Cell>
                    <Table.Cell>White</Table.Cell>
                    <Table.Cell>Laptop PC</Table.Cell>
                    <Table.Cell>$1999</Table.Cell>
                    <Table.Cell>
                        <a href="#" className="font-medium text-cyan-600 hover:underline dark:text-cyan-500">
                        Edit
                        </a>
                    </Table.Cell>
                    </Table.Row>
                    <Table.Row className="bg-white dark:border-gray-700 dark:bg-gray-800">
                    <Table.Cell className="whitespace-nowrap font-medium text-gray-900 dark:text-white">Magic Mouse 2</Table.Cell>
                    <Table.Cell>Black</Table.Cell>
                    <Table.Cell>Accessories</Table.Cell>
                    <Table.Cell>$99</Table.Cell>
                    <Table.Cell>
                        <a href="#" className="font-medium text-cyan-600 hover:underline dark:text-cyan-500">
                        Edit
                        </a>
                    </Table.Cell>
                    </Table.Row>
                </Table.Body>
            </Table>
        </main>
        
    );
  }
  