import logo from './logo.svg';
import './App.css';

function App() {
  return (
    <div className="login">
      <div className="overlap-wrapper">
        <div className="overlap">
          <div className="berlingske-media" alt="Berlingske media" />
          <div className="text-wrapper">Log in</div>
          <div className="overlap-group">
            <div className="div">Username</div>
          </div>
          <div className="div-wrapper">
            <div className="text-wrapper-2">Password</div>
          </div>
          <div className="frame">
            <div className="overlap-group-2">
              <div className="rectangle" />
              <div className="text-wrapper-3">Log in/admin</div>
            </div>
          </div>
          <div className="overlap-group-wrapper">
            <div className="overlap-group-2">
              <div className="rectangle" />
              <div className="text-wrapper-4">Log in</div>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
}

export default App;
