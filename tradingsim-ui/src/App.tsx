import {BrowserRouter as Router, Route, Routes} from 'react-router-dom'
import {Link} from "react-router";
import logo192Png from './assets/logo192.png'
import './App.css'
import {ShowPricesPage} from "./ShowPricesPage";

function Header() {
  return (
    <header className="app-header">
        <img style={{width: '60px', height: '60px'}} src={logo192Png}  alt="Mock Trading System App Logo"/>
        <h1>Mock Trading System App</h1>
        <ul>
            <li><Link to="/">Home</Link></li>
            <li><Link to="/prices">Prices</Link></li>
            <li><Link to="/stats">Stats</Link></li>
        </ul>
    </header>
  )
}

function HomePage() {
  return (
    <section id="center">
      <div>
        <p>
          Written by TheCodersCorner.com / Dave Cherry. See <a href="https://github.com/davetcc/MockTradingSimulator">the github repository</a>
        </p>
      </div>
    </section>
  )
}

function App() {
  return (
    <Router>
      <Header />
      <Routes>
        <Route path="/" element={<HomePage />} />
        <Route path="/prices" element={<ShowPricesPage />} />
      </Routes>
    </Router>
  )
}

export default App
