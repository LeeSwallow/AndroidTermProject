from mcp_logic import mcp
from dotenv import load_dotenv
import os

load_dotenv()

port: int = int(os.environ.get("PORT") or "8000") 
mcp.run("sse", port=port, host="0.0.0.0")