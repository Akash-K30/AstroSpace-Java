import React, { useState, useEffect, useRef, useCallback } from "react";
import "./AsteroidDodge.css";

const ARENA_WIDTH = 100; // percent-based coordinate space
const SHIP_WIDTH = 8;
const TICK_MS = 30;

let nextId = 0;

const AsteroidDodge = () => {
  const [shipX, setShipX] = useState(46);
  const [asteroids, setAsteroids] = useState([]);
  const [score, setScore] = useState(0);
  const [best, setBest] = useState(0);
  const [crashed, setCrashed] = useState(false);

  const shipXRef = useRef(shipX);
  const spawnTimerRef = useRef(0);
  const spawnIntervalRef = useRef(1100);
  const crashedRef = useRef(false);

  useEffect(() => {
    shipXRef.current = shipX;
  }, [shipX]);

  useEffect(() => {
    crashedRef.current = crashed;
  }, [crashed]);

  const moveShip = useCallback((dir) => {
    if (crashedRef.current) return;
    setShipX((x) => Math.min(100 - SHIP_WIDTH, Math.max(0, x + dir * 7)));
  }, []);

  // Keyboard controls
  useEffect(() => {
    const onKeyDown = (e) => {
      if (e.key === "ArrowLeft" || e.key.toLowerCase() === "a") moveShip(-1);
      if (e.key === "ArrowRight" || e.key.toLowerCase() === "d") moveShip(1);
    };
    window.addEventListener("keydown", onKeyDown);
    return () => window.removeEventListener("keydown", onKeyDown);
  }, [moveShip]);

  // Game loop
  useEffect(() => {
    const interval = setInterval(() => {
      if (crashedRef.current) return;

      spawnTimerRef.current += TICK_MS;

      setAsteroids((prev) => {
        let next = prev
          .map((a) => ({ ...a, y: a.y + a.speed }))
          .filter((a) => a.y < 108);

        // Spawn new asteroid
        if (spawnTimerRef.current >= spawnIntervalRef.current) {
          spawnTimerRef.current = 0;
          spawnIntervalRef.current = Math.max(500, spawnIntervalRef.current - 15);
          next = [
            ...next,
            {
              id: nextId++,
              x: Math.random() * (100 - 6),
              y: -8,
              size: 5 + Math.random() * 4,
              speed: 1.6 + Math.random() * 1.4,
            },
          ];
        }

        // Collision check + scoring for dodged asteroids
        const shipLeft = shipXRef.current;
        const shipRight = shipXRef.current + SHIP_WIDTH;

        let dodged = 0;
        let hit = false;

        for (const a of prev) {
          const passedThisTick =
            a.y < 92 && a.y + a.speed >= 92 && !hit;
          if (passedThisTick) dodged += 1;
        }

        for (const a of next) {
          if (a.y > 78 && a.y < 96) {
            const aLeft = a.x;
            const aRight = a.x + a.size;
            if (aRight > shipLeft && aLeft < shipRight) {
              hit = true;
            }
          }
        }

        if (hit) {
          setCrashed(true);
          setBest((b) => Math.max(b, score));
        } else if (dodged > 0) {
          setScore((s) => s + dodged);
        }

        return next;
      });
    }, TICK_MS);

    return () => clearInterval(interval);
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [score]);

  const restart = () => {
    setAsteroids([]);
    setScore(0);
    setShipX(46);
    spawnTimerRef.current = 0;
    spawnIntervalRef.current = 1100;
    setCrashed(false);
  };

  return (
    <div className="asteroid-game">
      <p className="asteroid-game__hint">
        Server's waking up — dodge some rocks while you wait 🛰️
      </p>

      <div className="asteroid-game__arena">
        <div className="asteroid-game__score">Score: {score}</div>

        {asteroids.map((a) => (
          <span
            key={a.id}
            className="asteroid-game__rock"
            style={{
              left: `${a.x}%`,
              top: `${a.y}%`,
              width: `${a.size}%`,
              height: `${a.size}%`,
            }}
          >
            ☄️
          </span>
        ))}

        <span
          className="asteroid-game__ship"
          style={{ left: `${shipX}%` }}
        >
          🚀
        </span>

        {crashed && (
          <div className="asteroid-game__overlay">
            <p>💥 Boom! Score: {score}</p>
            {best > 0 && <p className="asteroid-game__best">Best: {best}</p>}
            <button onClick={restart}>Try again</button>
          </div>
        )}
      </div>

      <div className="asteroid-game__controls">
        <button
          onPointerDown={() => moveShip(-1)}
          aria-label="Move left"
        >
          ◀
        </button>
        <button
          onPointerDown={() => moveShip(1)}
          aria-label="Move right"
        >
          ▶
        </button>
      </div>
    </div>
  );
};

export default AsteroidDodge;
