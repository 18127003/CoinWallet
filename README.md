<p><b>* BRIEF</b></p>
<p>CoinWallet is an application dedicated to managing and transfering crypto-currencies, specializing on Bitcoin.
The software focuses on constructing a determinate e-wallet, making the sophisticated transfer process abstract
to end users with minimal steps and security.</p>

<p><b>* CORE</b></p>
<p>CoinWallet is a mobile application for Android devices, which have limitations on both storage and calculating
power for a crypto algorithm. In the normal Bitcoin model, a Bitcoin node has to be a Bitcoin Core Node to verify
a transaction from others, which requires users to store all the blockchain from the beginning. This application,
instead, uses a simplified model of a Bitcoin node, introduced in Bitcoin white paper, which trade off the ability
to truly verified an incoming transaction for being lightweight.</p>
<p>This model still be able to make a valid transaction based on the truncated data it holds because it rely on
remote Bitcoin Nodes who support simplified models. It fetchs the data from a list of nodes arbitrarily, so most
of the time, it is the trustworthiness of remote ones that count.</p>
<p>The wallet created using this application will follow <b>BIP-44</b> standard, a hierachy determinate wallet which 
support multiple accounts as well as multiple crypto-currencies in one wallet for simple management.</p>

<p><b>* FUNCTION</b></p>
<p><b>1. Create account</b></p>
<p>The users have to give their account a name, and choose the network for the account. As for now, available networks
option are Bitcoin Mainnet and Bitcoin Testnet.</p>

<p><b>2. Restore account</b></p>
<p>If the user deleted the application or moving to another device, they can retrieve their wallet thanks to the mnemonic
that comes along with each account, which is a comprehensive combination of 12 unique English defined words. Besides,
users have to specify some addition information like a new name, the network, and also the index of the old wallet.</p>

<p><b>3. Make a transaction</b></p>
<p>A Bitcoin transaction includes inputs and outputs. An output of a previous transaction will be an input for another 
transaction, which make it a blockchain. The transaction input contain a script to verify the recipient to know whether
they have the responding private key.</p>
<p>In CoinWallet application, the transaction is created in the device itself, then it will be broadcasted to the network
of remote nodes. If the nodes are trustworthy, then the transaction will be acknowledged by the blockchain when the block
is mined.</p>
<p>By specify a wallet adress of the recipient, the user can transfer them money. Anyway, they have to spend a small amount 
of fee for block miner. The application offers 2 mode of transfering Bitcoin.</p>
<p>In the normal way, user create the transaction and broadcast it to the network.</p>
<p>In Bluetooth transfer, user create the transaction and broadcast it to the recipient so that they can broadcast it to the
network. This will help when user don't have access to the Internet while the recipient does.</p>
<p>In both way, if the people who is responsible for broadcasting the transaction can't connect to the Internet, the transaction
will be broadcasted when the device go online again. This software also allow sending to multiple recipients.</p>
<p><b>Sending Strategy</b></p>
<p>The account balance is a collection of inputs received, which will not perfectly match the amount that user is going to send.
The wallet choose smallest usable inputs until their sum up equal or larger than the sending amount, which help avoid flooding the
wallet with very small inputs. The remainder will be put to an output which send to the user's wallet again.</p>
