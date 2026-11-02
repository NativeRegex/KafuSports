// KAFU Sports — animated night stadium (drawn in canvas)
window.addEventListener('DOMContentLoaded', function () {
    document.querySelectorAll('#bg-canvas').forEach(c => c.remove());

    const canvas = document.createElement('canvas');
    canvas.id = 'bg-canvas';
    canvas.style.cssText = 'position:fixed; top:0; left:0; width:100%; height:100%; z-index:0; pointer-events:none;';
    document.body.prepend(canvas);

    const ctx = canvas.getContext('2d');
    let W, H;
    function resize() {
        W = canvas.width  = window.innerWidth;
        H = canvas.height = window.innerHeight;
    }
    window.addEventListener('resize', resize);
    resize();

    // ---------- Colour palette ----------
    const NIGHT_TOP     = '#050810';
    const NIGHT_MID     = '#0B1A25';
    const STAND_DARK    = '#141E28';
    const STAND_LIGHT   = '#1C2A38';
    const CROWD_DOT     = 'rgba(255, 220, 120, 0.55)';
    const TURF_DARK     = '#183A26';
    const TURF_MID      = '#1F5A34';
    const TURF_LIGHT    = '#2A7547';
    const LINE_COLOR    = 'rgba(240, 255, 245, 0.55)';
    const GREEN_KIT     = '#4DD086';
    const GOLD_KIT      = '#E4B93A';
    const FLOOD_COLOR   = 'rgba(255, 250, 220, 0.95)';

    // ---------- Pitch geometry ----------
    const STAND_H       = H * 0.22;         // top stands occupy the top 22% of screen
    const PITCH_TOP     = STAND_H;
    const PITCH_H       = H - STAND_H;

    // ---------- Crowd dots (stands) ----------
    const crowd = [];
    function buildCrowd() {
        crowd.length = 0;
        const rows = 14;
        const rowH = STAND_H / (rows + 1);
        for (let r = 0; r < rows; r++) {
            const y = r * rowH + 6;
            const cols = Math.floor(W / 10);
            for (let c = 0; c < cols; c++) {
                if (Math.random() < 0.35) continue;
                const x = c * 10 + (r % 2 === 0 ? 0 : 5);
                crowd.push({
                    x, y,
                    r: 1.4 + Math.random() * 1.6,
                    color: Math.random() > 0.85 ? '#FF6B6B' : CROWD_DOT
                });
            }
        }
    }
    buildCrowd();

    // ---------- Players ----------
    const players = [];
    const TEAM_SIZE = 5;

    function makePlayer(team, idx) {
        const baseX = (W / (TEAM_SIZE + 1)) * (idx + 1);
        const baseY = team === 'green'
            ? PITCH_TOP + PITCH_H * 0.72
            : PITCH_TOP + PITCH_H * 0.28;
        return {
            team, baseX, baseY,
            x: baseX, y: baseY,
            phase: Math.random() * Math.PI * 2,
            color: team === 'green' ? GREEN_KIT : GOLD_KIT
        };
    }

    function initPlayers() {
        players.length = 0;
        for (let i = 0; i < TEAM_SIZE; i++) {
            players.push(makePlayer('green', i));
            players.push(makePlayer('gold', i));
        }
    }
    initPlayers();

    // ---------- Ball ----------
    const ball = {
        x: W / 2,
        y: PITCH_TOP + PITCH_H / 2,
        vx: 2.4, vy: 1.7, r: 6
    };

    // ---------- Draw: sky + stands ----------
    function drawStands() {
        // Night sky gradient
        const sky = ctx.createLinearGradient(0, 0, 0, STAND_H);
        sky.addColorStop(0, NIGHT_TOP);
        sky.addColorStop(1, NIGHT_MID);
        ctx.fillStyle = sky;
        ctx.fillRect(0, 0, W, STAND_H);

        // Stands (rows)
        const rows = 14;
        const rowH = STAND_H / (rows + 1);
        for (let r = 0; r < rows; r++) {
            ctx.fillStyle = r % 2 === 0 ? STAND_DARK : STAND_LIGHT;
            ctx.fillRect(0, r * rowH, W, rowH);
        }

        // Crowd dots
        for (const c of crowd) {
            ctx.fillStyle = c.color;
            ctx.beginPath();
            ctx.arc(c.x, c.y, c.r, 0, Math.PI * 2);
            ctx.fill();
        }

        // Roof shadow line at bottom of stands
        const shadow = ctx.createLinearGradient(0, STAND_H - 30, 0, STAND_H);
        shadow.addColorStop(0, 'rgba(0,0,0,0)');
        shadow.addColorStop(1, 'rgba(0,0,0,0.6)');
        ctx.fillStyle = shadow;
        ctx.fillRect(0, STAND_H - 30, W, 30);
    }

    // ---------- Draw: floodlights ----------
    function drawFloodlight(x, y, spread, intensity) {
        // Post
        ctx.strokeStyle = '#3A3A3A';
        ctx.lineWidth = 2;
        ctx.beginPath();
        ctx.moveTo(x, STAND_H);
        ctx.lineTo(x, y);
        ctx.stroke();

        // Light head
        ctx.fillStyle = '#E8E8E8';
        ctx.fillRect(x - 14, y - 8, 28, 10);

        // Glow beam
        const glow = ctx.createRadialGradient(x, y, 8, x, y, spread);
        glow.addColorStop(0, `rgba(255, 250, 220, ${intensity})`);
        glow.addColorStop(0.4, `rgba(255, 240, 180, ${intensity * 0.35})`);
        glow.addColorStop(1, 'rgba(255, 240, 180, 0)');
        ctx.fillStyle = glow;
        ctx.beginPath();
        ctx.arc(x, y, spread, 0, Math.PI * 2);
        ctx.fill();
    }

    function drawAllFloodlights(t) {
        const pulse = 0.55 + Math.sin(t * 0.0015) * 0.15;
        drawFloodlight(W * 0.12, 40, 260, pulse);
        drawFloodlight(W * 0.88, 40, 260, pulse);
        drawFloodlight(W * 0.35, 20, 200, pulse * 0.8);
        drawFloodlight(W * 0.65, 20, 200, pulse * 0.8);
    }

    // ---------- Draw: pitch ----------
    function drawPitch() {
        // Base gradient
        const base = ctx.createRadialGradient(
            W / 2, PITCH_TOP + PITCH_H / 2, 40,
            W / 2, PITCH_TOP + PITCH_H / 2, Math.max(W, PITCH_H) * 0.85
        );
        base.addColorStop(0, TURF_MID);
        base.addColorStop(0.6, TURF_DARK);
        base.addColorStop(1, '#0A1408');
        ctx.fillStyle = base;
        ctx.fillRect(0, PITCH_TOP, W, PITCH_H);

        // Mowing stripes (perspective squash at top)
        const bands = 16;
        for (let i = 0; i < bands; i++) {
            if (i % 2 === 0) continue;
            const t0 = i / bands;
            const t1 = (i + 1) / bands;
            const y0 = PITCH_TOP + PITCH_H * Math.pow(t0, 1.15);
            const y1 = PITCH_TOP + PITCH_H * Math.pow(t1, 1.15);
            const grad = ctx.createLinearGradient(0, y0, 0, y1);
            grad.addColorStop(0, TURF_LIGHT);
            grad.addColorStop(1, TURF_MID);
            ctx.fillStyle = grad;
            ctx.fillRect(0, y0, W, y1 - y0);
        }

        // Pitch markings
        ctx.strokeStyle = LINE_COLOR;
        ctx.lineWidth = Math.max(1.2, W / 900);

        const padX = W * 0.05;
        const padTop = PITCH_TOP + PITCH_H * 0.10;
        const padBottom = H - PITCH_H * 0.05;

        // Boundary (perspective)
        ctx.beginPath();
        ctx.moveTo(padX, padTop);
        ctx.lineTo(W - padX, padTop);
        ctx.lineTo(W - padX * 0.6, padBottom);
        ctx.lineTo(padX * 0.6, padBottom);
        ctx.closePath();
        ctx.stroke();

        // Halfway line
        const midY = PITCH_TOP + PITCH_H * 0.55;
        ctx.beginPath();
        ctx.moveTo(padX * 0.8, midY);
        ctx.lineTo(W - padX * 0.8, midY);
        ctx.stroke();

        // Center circle (ellipse for perspective)
        ctx.beginPath();
        ctx.ellipse(W / 2, midY, W * 0.085, PITCH_H * 0.05, 0, 0, Math.PI * 2);
        ctx.stroke();

        // Center spot
        ctx.fillStyle = LINE_COLOR;
        ctx.beginPath();
        ctx.ellipse(W / 2, midY, 3, 2, 0, 0, Math.PI * 2);
        ctx.fill();

        // Far penalty box (top)
        const topBoxW = W * 0.18;
        const topBoxH = PITCH_H * 0.08;
        ctx.strokeRect(W / 2 - topBoxW / 2, padTop, topBoxW, topBoxH);

        // Near penalty box (bottom, larger)
        const botBoxW = W * 0.32;
        const botBoxH = PITCH_H * 0.14;
        ctx.strokeRect(W / 2 - botBoxW / 2, padBottom - botBoxH, botBoxW, botBoxH);
    }

    // ---------- Draw: player with shadow ----------
    function drawPlayer(p) {
        ctx.fillStyle = 'rgba(0,0,0,0.28)';
        ctx.beginPath();
        ctx.ellipse(p.x, p.y + 30, 12, 4, 0, 0, Math.PI * 2);
        ctx.fill();

        const scale = 0.85 + ((p.y - PITCH_TOP) / PITCH_H) * 0.5;
        ctx.strokeStyle = p.color;
        ctx.fillStyle = p.color;
        ctx.lineWidth = 2 * scale;

        ctx.beginPath();
        ctx.arc(p.x, p.y, 5 * scale, 0, Math.PI * 2);
        ctx.fill();

        ctx.beginPath();
        ctx.moveTo(p.x, p.y + 5 * scale);
        ctx.lineTo(p.x, p.y + 20 * scale);
        ctx.stroke();

        const swing = Math.sin(p.phase) * 8 * scale;
        ctx.beginPath();
        ctx.moveTo(p.x, p.y + 20 * scale);
        ctx.lineTo(p.x - 6 * scale + swing, p.y + 32 * scale);
        ctx.moveTo(p.x, p.y + 20 * scale);
        ctx.lineTo(p.x + 6 * scale - swing, p.y + 32 * scale);
        ctx.stroke();

        ctx.beginPath();
        ctx.moveTo(p.x, p.y + 9 * scale);
        ctx.lineTo(p.x - 7 * scale - swing, p.y + 16 * scale);
        ctx.moveTo(p.x, p.y + 9 * scale);
        ctx.lineTo(p.x + 7 * scale + swing, p.y + 16 * scale);
        ctx.stroke();
    }

    // ---------- Main loop ----------
    let tStart = performance.now();

    function frame(t) {
        drawStands();
        drawPitch();
        drawAllFloodlights(t);

        // Ball movement
        ball.x += ball.vx;
        ball.y += ball.vy;
        if (ball.x < 40 || ball.x > W - 40) ball.vx *= -1;
        if (ball.y < PITCH_TOP + 40 || ball.y > H - 40) ball.vy *= -1;

        // Ball shadow
        ctx.fillStyle = 'rgba(0,0,0,0.35)';
        ctx.beginPath();
        ctx.ellipse(ball.x, ball.y + 5, ball.r, ball.r * 0.4, 0, 0, Math.PI * 2);
        ctx.fill();

        // Ball
        ctx.fillStyle = '#FFFFFF';
        ctx.beginPath();
        ctx.arc(ball.x, ball.y, ball.r, 0, Math.PI * 2);
        ctx.fill();

        // Players
        for (const p of players) {
            p.phase += 0.14;
            p.x = p.baseX + (ball.x - p.baseX) * 0.15;
            p.y = p.baseY + Math.sin(p.phase * 0.3) * 5;
            drawPlayer(p);
        }

        requestAnimationFrame(frame);
    }

    requestAnimationFrame(frame);
});

// ---- Count-up animation for stat numbers ----
window.addEventListener('DOMContentLoaded', function () {
    document.querySelectorAll('[data-count]').forEach(function (el) {
        const target = parseInt(el.dataset.count, 10) || 0;
        if (target === 0) { el.textContent = '0'; return; }
        let current = 0;
        const step = Math.max(1, Math.floor(target / 40));
        const interval = setInterval(() => {
            current += step;
            if (current >= target) {
                current = target;
                clearInterval(interval);
            }
            el.textContent = current;
        }, 25);
    });
});
